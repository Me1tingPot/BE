package meltingpot.server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import meltingpot.server.util.ResponseCode;

@Getter
@AllArgsConstructor
public class MailSendException extends RuntimeException{
    private final ResponseCode responseCode;
}
