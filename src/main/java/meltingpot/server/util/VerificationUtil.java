package meltingpot.server.util;

public class VerificationUtil {

    // 유저 이메일
    public static final String USERNAME_REGEXP = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
    public static final String PASSWORD_REGEXP = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d$`~!@$!%#^?&\\(\\)-_=+]{8,20}$\n";

    // 유저 닉네임
    public static final String NAME_REGEXP = "^.{1,10}$";

    // 유저 프로필 소개
    public static final String BIO_REGEXP = "^.{1,50}$";

}
