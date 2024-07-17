package meltingpot.server.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import meltingpot.server.util.ResponseCode;

@Getter
@RequiredArgsConstructor
public class IllegalArgumentException extends RuntimeException {

    private final ResponseCode responseCode;
}
