package meltingpot.server.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meltingpot.server.chat.dto.ChatMessageGetResponse;
import meltingpot.server.chat.dto.ChatMessageSendRequest;
import meltingpot.server.chat.dto.UnreadChatMessageSendDTO;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.chat.*;
import meltingpot.server.domain.entity.chat.enums.Role;
import meltingpot.server.domain.entity.party.Party;
import meltingpot.server.domain.repository.AccountRepository;
import meltingpot.server.domain.repository.chat.ChatMessageRepository;
import meltingpot.server.domain.repository.chat.ChatRoomRepository;
import meltingpot.server.domain.repository.chat.ChatRoomUserRepository;
import meltingpot.server.domain.repository.chat.SocketSessionRepository;
import meltingpot.server.domain.repository.party.PartyRepository;
import meltingpot.server.exception.BadRequestException;
import meltingpot.server.exception.ResourceNotFoundException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static meltingpot.server.util.ResponseCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WebSocketService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AccountRepository accountRepository;
    private final PartyRepository partyRepository;
    private final SocketSessionRepository socketSessionRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final SimpMessagingTemplate template;

    @Transactional
    public ChatMessage createChatMessage(String sessionId, ChatMessageSendRequest chatMessageSendRequest) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessageSendRequest.chatRoomId())
                .orElseThrow(() -> new ResourceNotFoundException(CHAT_ROOM_NOT_FOUND));

        Party party = partyRepository.findByChatRoomId(chatRoom.getId())
                .orElseThrow(() -> new ResourceNotFoundException(PARTY_NOT_FOUND));

        Account account = accountRepository.findByUsername(socketSessionRepository.findBySessionId(sessionId).getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(ACCOUNT_NOT_FOUND));

        Role role = (party.getAccount().getId().equals(account.getId()))
                ? Role.LEADER
                : Role.MEMBER;

        ChatMessage newChatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .content(chatMessageSendRequest.content())
                .account(account)
                .role(role)
                .build();

        return chatMessageRepository.save(newChatMessage);
    }

    @Transactional(rollbackFor = Exception.class)
    public void convertAndSend(ChatMessage chatMessage) {

        List<SocketSession> socketSessions = socketSessionRepository.findByChatRoomId(chatMessage.getChatRoom().getId());
        List<String> socketSessionUsernames = socketSessions.stream()
                .map(SocketSession::getUsername)
                .toList();

        List<ChatRoomUser> chatRoomUsers = chatRoomUserRepository.findAllByChatRoom(chatMessage.getChatRoom());

        chatRoomUsers.stream()
                .map(this::getAccount)
                .filter(account -> !isSender(account, chatMessage))
                .forEach(account -> Optional.of(socketSessionUsernames.contains(account.getUsername()))
                        .filter(Boolean::booleanValue)
                        .ifPresentOrElse(
                                isPresent -> template.convertAndSend("/sub/chat" + chatMessage.getChatRoom().getId(), ChatMessageGetResponse.from(chatMessage)),
                                () -> sendNotificationOrMessage(account, chatMessage)
                        ));
    }

    private Account getAccount(ChatRoomUser chatRoomUser) {
        return accountRepository.findById(chatRoomUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(ACCOUNT_NOT_FOUND));
    }

    private boolean isSender(Account account, ChatMessage chatMessage) {
        return account.getId().equals(chatMessage.getAccount().getId());
    }

    private void sendNotificationOrMessage(Account account, ChatMessage chatMessage) {
        int unreadMessageCnt = updateUnreadMessageCnt(account, chatMessage);
        Optional.ofNullable(socketSessionRepository.findByUsernameAndChatRoomId(account.getUsername(), 0L))
                .ifPresentOrElse(
                        session -> sendMessageToChatList(account, chatMessage, unreadMessageCnt),
                        () -> sendNotification(account, chatMessage)
                );
    }

    private int updateUnreadMessageCnt(Account account, ChatMessage chatMessage) {
        ChatRoomUser chatRoomUser = chatRoomUserRepository.findChatRoomUserByUserIdAndChatRoomId(account.getId(), chatMessage.getChatRoom().getId())
                .orElseThrow(() -> new ResourceNotFoundException(CHAT_ROOM_USER_NOT_FOUND));

        return chatRoomUser.updateUnreadMessageCnt();
    }

    /** 채팅방 목록에 있는 경우 */
    private void sendMessageToChatList(Account account, ChatMessage chatMessage, int unreadMessageCnt) {
        template.convertAndSend("/sub/list" + account.getId(),
                new UnreadChatMessageSendDTO(chatMessage.getChatRoom().getId(), unreadMessageCnt)
        );
    }

    /** 채팅방 목록에도 없는 경우 알림 전송 */
    private void sendNotification(Account account, ChatMessage chatMessage) {
    }

    @Transactional
    public SocketSession onConnect(MessageHeaders headers) {
        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) headers.get("simpUser");
        String simpSessionId = headers.get("simpSessionId").toString();

        if (user == null) {
            throw new BadRequestException(SOCKET_CONNECT_HEADER_CHECK_FAIL);
        } else {
            UserDetails attributes = (UserDetails) user.getPrincipal();

            String username = attributes.getUsername();

            SocketSession newSocketSession = SocketSession.builder()
                    .sessionId(simpSessionId)
                    .username(username)
                    .build();

            return socketSessionRepository.save(newSocketSession);
        }
    }

    @Transactional
    public SocketSession onSubscribe(MessageHeaders headers) {
        String simpSessionId = headers.get("simpSessionId").toString();

        // Long chatRoomId = Long.parseLong(accessor.getFirstNativeHeader("chatRoomId"));
        String chatRoomIdStr = (getChatRoomId(
                Optional.ofNullable((String) headers.get("simpDestination"))
                        .orElse(CHAT_ROOM_NOT_FOUND.getDetail())));

        Long chatRoomId;
        try {
            chatRoomId = Long.parseLong(chatRoomIdStr);
        } catch (NumberFormatException e) {
            throw new BadRequestException(CHAT_ROOM_NOT_FOUND);
        }

        SocketSession socketSession = socketSessionRepository.findBySessionId(simpSessionId);
        socketSession.setChatRoomId(chatRoomId);

        return socketSessionRepository.save(socketSession);
    }

    private String getChatRoomId(String destination) {
        String[] test = destination.split("/");
        String chatRoomId = test[test.length - 1];

        return chatRoomId;
    }

    public void onDisconnect(String sessionId) {
        SocketSession socketSession = socketSessionRepository.findBySessionId(sessionId);

        socketSessionRepository.delete(socketSession);
    }
}