package meltingpot.server.util.apiPayload.code;


import lombok.AllArgsConstructor;
import lombok.Getter;
import meltingpot.server.util.apiPayload.ErrorReasonDTO;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    //S3 관련
    S3_OBJECT_NOT_FOUND(HttpStatus.NOT_FOUND,  "이미지가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String detail;


    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder().message(detail).isSuccess(false).build();
    }
    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .code(name())
                .message(detail)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
