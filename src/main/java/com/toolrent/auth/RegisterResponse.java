package com.toolrent.auth;

public record RegisterResponse(String token, String userName, String email) {
}
