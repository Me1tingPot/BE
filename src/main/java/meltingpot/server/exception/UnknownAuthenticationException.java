package meltingpot.server.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UnknownAuthenticationException extends RuntimeException {

    private final String detail;
}