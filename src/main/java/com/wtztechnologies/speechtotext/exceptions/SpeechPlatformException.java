package com.wtztechnologies.speechtotext.exceptions;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpeechPlatformException extends SpeechInternalException {

    HttpStatus status;

    public SpeechPlatformException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public SpeechPlatformException(HttpStatus status, String message, Object... arguments) {
        super(String.format(message, arguments));
        this.status = status;
    }

}
