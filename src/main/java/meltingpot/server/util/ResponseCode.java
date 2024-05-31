package meltingpot.server.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    /* 200 OK : 요청 성공 */
    SIGNIN_SUCCESS(OK, "로그인 성공"),
    SIGNOUT_SUCCESS(OK, "로그아웃 성공"),
    REISSUE_TOKEN_SUCCESS(OK, "토큰 재발급 성공"),
    PARTY_FETCH_SUCCESS(OK, "파티 정보 불러오기 성공"),
    PARTY_SEARCH_SUCCESS(OK, "파티 검색 성공"),
    PARTY_JOIN_SUCCESS(OK, "파티 참여 성공"),
    AREA_FETCH_SUCCESS(OK, "지역 조회 성공"),
    PARTY_DELETE_SUCCESS(OK, "파티 삭제 성공"),
    PARTY_MODIFY_SUCCESS(OK, "파티 수정 성공"),
    CHAT_ALARM_UPDATE_SUCCESS(OK, "채팅 알림 설정 수정 성공"),
    CHAT_DETAIL_GET_SUCCESS(OK, "채팅방 상단 조회 성공"),
    CHAT_MESSAGE_GET_SUCCESS(OK, "채팅 메세지 조회 성공"),
    POST_LIST_FETCH_SUCCESS(OK,"게시글 목록 불러오기 성공"),
    POST_LIST_FETCH_SUCCEESS(OK,"게시글 목록 불러오기 성공"),
    READ_PROFILE_SUCCESS(OK, "사용자 프로필 불러오기 성공"),


    /* 201 CREATED : 요청 성공, 자원 생성 */
    SIGNUP_SUCCESS(CREATED, "회원가입 성공"),
    CREATE_CHAT_ROOM_SUCCESS(CREATED, "채팅방 생성 성공"),
    CREATE_POST_SUCCESS(CREATED,"게시물 작성 성공"),
    CREATE_COMMENT_SUCCESS(CREATED,"댓글 작성 성공"),
    PARTY_REPORT_SUCCESS(CREATED, "파티 신고 성공"),
    PARTY_CREATE_SUCCESS(CREATED, "파티 생성 성공"),
    IMAGE_URL_GENERATE_SUCCESS(CREATED, "이미지 URL 생성 성공"),


    /* 400 BAD_REQUEST : 잘못된 요청 */
    MAIL_SEND_FAIL(BAD_REQUEST, "메일 전송 실패"),
    AUTH_NUMBER_INCORRECT(BAD_REQUEST, "인증 번호가 옳지 않습니다"),
    PARTY_NOT_OPEN(BAD_REQUEST, "모집중인 파티가 아닙니다"),
    PARTY_FULL(BAD_REQUEST, "파티 인원이 가득 찼습니다"),
    PARTY_ALREADY_JOINED(BAD_REQUEST, "이미 참여한 파티입니다"),
    PARTY_REPORT_SELF(BAD_REQUEST, "본인이 작성한 파티는 신고할 수 없습니다"),
    PARTY_REPORT_ALREADY(BAD_REQUEST, "이미 신고한 파티입니다"),
    PARTY_SEARCH_FAIL(BAD_REQUEST, "파티 검색 실패"),
    PARTY_INVALID_QUERY(BAD_REQUEST, "검색어는 2글자 이상 30글자 이하로 입력해주세요"),


    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_AUTH_TOKEN(UNAUTHORIZED, "권한 정보가 없는 토큰입니다"),
    INVALID_ACCOUNT(UNAUTHORIZED, "계정이 비활성화 되었습니다"),
    CREDENTIALS_EXPIRED(UNAUTHORIZED, "비밀번호 유효기간이 만료되었습니다"),
    UNKNOWN_AUTHENTICATION_ERROR(UNAUTHORIZED, "알 수 없는 이유로 로그인에 실패했습니다"),


    /* 403 FORBIDDEN : 권한이 없는 사용자 */
    INVALID_REFRESH_TOKEN(FORBIDDEN, "잘못된 REFRESH 토큰입니다"),
    BLACKLIST_MEMBER(FORBIDDEN, "접근할 수 없는 계정입니다"),
    HOLDING_WITHDRAWAL(FORBIDDEN, "30일 이내에 탈퇴한 계정입니다"),
    SIGNOUT_FAIL_REFRESH_TOKEN(FORBIDDEN, "본인의 REFRESH 토큰으로만 로그아웃할 수 있습니다"),
    S3_OBJECT_NOT_FOUND(NOT_FOUND, "REFRESH 토큰 정보를 찾을 수 없습니다"),
    PARTY_DELETE_NOT_OWNER(FORBIDDEN, "파티를 삭제할 권한이 없습니다"),
    PARTY_MODIFY_NOT_OWNER(FORBIDDEN, "파티를 수정할 권한이 없습니다"),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    ACCOUNT_NOT_FOUND(NOT_FOUND, "계정 정보를 찾을 수 없습니다"),
    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "REFRESH 토큰 정보를 찾을 수 없습니다"),
    PARTY_NOT_FOUND(NOT_FOUND, "파티 정보를 찾을 수 없습니다"),
    CHAT_ROOM_NOT_FOUND(NOT_FOUND, "채팅방 정보를 찾을 수 없습니다"),
    POST_NOT_FOUND(NOT_FOUND,"게시글을 찾을 수 없습니다"),


    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    EMAIL_DUPLICATION(CONFLICT, "이미 사용 중인 이메일입니다"),
    POST_REPORT_DUPLICATION(CONFLICT, "이미 신고한 게시글입니다");


    private final HttpStatus httpStatus;
    private final String detail;


}