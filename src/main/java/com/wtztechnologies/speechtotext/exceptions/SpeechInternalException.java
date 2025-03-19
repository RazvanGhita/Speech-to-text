package com.wtztechnologies.speechtotext.exceptions;

import lombok.Getter;

@Getter
public class SpeechInternalException extends RuntimeException {

    public SpeechInternalException(String message) {
        super(message);
    }

    public SpeechInternalException(String message, Object... arguments) {
        super(String.format(message, arguments));
    }

    public SpeechInternalException(Throwable cause, String message, Object... arguments) {
        super(String.format(message, arguments), cause);
    }

}
