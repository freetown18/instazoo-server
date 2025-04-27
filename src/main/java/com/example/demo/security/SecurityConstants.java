package com.example.demo.security;

public class SecurityConstants {

    public static final String SIGN_UP_URLS = "/api/auth/*";

//    public static final String SECRET = "ThisIsMySecretKeyHereWithSufficientLengthForHS512Algorithm"; // минимум 64 байта
    public static final String TOKEN_PREFIX = "Bearer "; // Пробел обязателен
    public static final String HEADER_STRING = "Authorization";
    public static final String CONTENT_TYPE = "application/json";
    public static final long EXPIRATION_TIME = 86400000; // 1 сутки

}