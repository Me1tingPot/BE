package meltingpot.server.util.apiPayload.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import meltingpot.server.util.apiPayload.ErrorReasonDTO;
import meltingpot.server.util.apiPayload.code.BaseErrorCode;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}
