package com.example.studysecurity;

import lombok.Getter;

@Getter
public class GoogleAccessTokenRequest {
    private final String code;
    private final String client_id;
    private final String client_secret;
    private final String redirect_uri;
    private final String grant_type;

    public GoogleAccessTokenRequest(String code, String clientId, String clientSecret, String redirectUri, String authorizationCode) {
        this.code = code;
        this.client_id = clientId;
        this.client_secret = clientSecret;
        this.redirect_uri = redirectUri;
        this.grant_type = authorizationCode; // "authorization_code"
    }
}
