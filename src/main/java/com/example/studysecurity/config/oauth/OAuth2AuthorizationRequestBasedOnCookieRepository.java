package com.example.studysecurity.config.oauth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import org.springframework.web.util.WebUtils;

import java.util.Base64;

@Component
public class OAuth2AuthorizationRequestBasedOnCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String OAUTH2_AUTH_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    public static final int COOKIE_EXPIRE_SECONDS = 180;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTH_REQUEST_COOKIE_NAME);
        if (cookie != null) {
            byte[] decodedBytes = Base64.getDecoder().decode(cookie.getValue());
            return (OAuth2AuthorizationRequest) SerializationUtils.deserialize(decodedBytes);
        }
        return null;
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }
        byte[] serializedAuthRequest = SerializationUtils.serialize(authorizationRequest);
        String encodedAuthRequest = Base64.getEncoder().encodeToString(serializedAuthRequest);
        Cookie authCookie = new Cookie(OAUTH2_AUTH_REQUEST_COOKIE_NAME, encodedAuthRequest);
        authCookie.setPath("/");
        authCookie.setHttpOnly(true);
        authCookie.setMaxAge(COOKIE_EXPIRE_SECONDS);
        response.addCookie(authCookie);

        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
        if (redirectUriAfterLogin != null && !redirectUriAfterLogin.isEmpty()) {
            Cookie redirectCookie = new Cookie(REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin);
            redirectCookie.setPath("/");
            redirectCookie.setMaxAge(COOKIE_EXPIRE_SECONDS);
            response.addCookie(redirectCookie);
        }
    }


    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie authCookie = WebUtils.getCookie(request, OAUTH2_AUTH_REQUEST_COOKIE_NAME);
        if (authCookie != null) {
            authCookie.setValue("");
            authCookie.setPath("/");
            authCookie.setMaxAge(0);
            response.addCookie(authCookie);
        }
        Cookie redirectCookie = WebUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME);
        if (redirectCookie != null) {
            redirectCookie.setValue("");
            redirectCookie.setPath("/");
            redirectCookie.setMaxAge(0);
            response.addCookie(redirectCookie);
        }
    }
}
