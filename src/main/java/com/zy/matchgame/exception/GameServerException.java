package com.zy.matchgame.exception;

import com.zy.matchgame.error.GameServerError;
import lombok.Getter;
import lombok.Setter;

public class GameServerException extends RuntimeException {

    @Getter
    @Setter
    private Integer code;

    private String message;

    public GameServerException(GameServerError error) {
        super(error.getErrorDesc());
        this.code = error.getErrorCode();
    }
}
