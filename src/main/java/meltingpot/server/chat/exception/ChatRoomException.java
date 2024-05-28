package meltingpot.server.chat.exception;

import lombok.Getter;
import meltingpot.server.util.ResponseCode;

@Getter
public class ChatRoomException extends RuntimeException {
    private final ResponseCode errorCode;

    public ChatRoomException(ResponseCode errorCode) {
        this.errorCode = errorCode;
    }
}
