package com.toolrent.auth.web;

public record RegisterResponse(String token, String userName, String email) {
}
