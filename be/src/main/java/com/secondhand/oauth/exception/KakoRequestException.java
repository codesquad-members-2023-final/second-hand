package com.secondhand.oauth.exception;

public class KakoRequestException extends OAuthException {
    public KakoRequestException() {
        super("카카오 request 실패.");
    }
}
