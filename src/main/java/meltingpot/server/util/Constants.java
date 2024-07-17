package meltingpot.server.util;

public final class Constants {

    // 이메일 인증 시도 제한 횟수
    public static final Integer LIMIT_ATTEMPT_COUNT = 5;

    // 이메일 인증 제한 쿨타임 시간(분)
    public static final Integer COOL_TIME_MINUTE = 5;

    // 이메일 인증 번호 확인 제한 시간(분)
    public static final Integer AUTH_TIME_LIMIT = 10;

    // 무한스크롤 페이지 디폴트 사이즈
    public static final Integer PAGE_DEFAULT_SIZE = 7;

    // 기본 프로필 이미지키
    public static final String DEFAULT_PROFILE_IMAGE_KEY = "65352995-3744-4482-8d0f-8b7baf5d0903";

}
