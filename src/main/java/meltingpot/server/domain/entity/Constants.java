package meltingpot.server.domain.entity;

public final class Constants {

    // 이메일 인증 시도 제한 횟수
    public static final Integer LIMIT_ATTEMPT_COUNT = 5;

    // 이메일 인증 제한 쿨타임 시간(분)
    public static final Integer COOL_TIME_MINUTE = 5;

    // 이메일 인증 번호 확인 제한 시간(분)
    public static final Integer AUTH_TIME_LIMIT = 10;

}
