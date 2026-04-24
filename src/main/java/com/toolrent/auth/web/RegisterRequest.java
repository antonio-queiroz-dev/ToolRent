package com.toolrent.auth.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String companyName,
        @NotBlank String userName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password
) {
}
