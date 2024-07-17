package meltingpot.server.util.apiPayload.exception;
import meltingpot.server.util.apiPayload.code.BaseErrorCode;


public class S3Exception extends GeneralException{

    public S3Exception(BaseErrorCode code) {
        super(code);
    }
}
