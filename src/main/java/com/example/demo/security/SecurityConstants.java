package com.example.demo.security;

public class SecurityConstants {
    public static final String SECRET = "generateThem!";
    public static final long EXPIRATION_TIME = 1_800_000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/user/create";
}
