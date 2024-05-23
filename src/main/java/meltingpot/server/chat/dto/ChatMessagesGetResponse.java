package meltingpot.server.chat.dto;

public record ChatMessagesGetResponse(
        String message,
        Boolean isLeader,
        String imageKey,
        String leaderName
) {
}
