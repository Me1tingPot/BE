package meltingpot.server.util.apiPayload.code;

import meltingpot.server.util.apiPayload.ErrorReasonDTO;

public interface BaseErrorCode {

    public ErrorReasonDTO getReason();

    public ErrorReasonDTO getReasonHttpStatus();
}