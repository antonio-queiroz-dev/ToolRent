package com.toolrent.auth;

import com.toolrent.AbstractIntegrationTest;
import com.toolrent.user.persistence.UserEntity;
import com.toolrent.user.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AuthControllerTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void signup_withValidData_returns201AndToken() throws Exception {
        String body = """
                {
                  "companyName": "Locadora Silva",
                  "userName": "João Silva",
                  "email": "joao@locadorasilva.com",
                  "password": "senha12345"
                }
                """;

        mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.userName").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@locadorasilva.com"));
    }

    @Test
    void signup_persistsUserWithHashedPassword() throws Exception {
        String body = """
                {
                  "companyName": "Locadora Silva",
                  "userName": "João Silva",
                  "email": "hash@locadorasilva.com",
                  "password": "senha12345"
                }
                """;

        mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        UserEntity user = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals("hash@locadorasilva.com"))
                .findFirst()
                .orElseThrow();

        assertThat(user.getTenantId()).isNotNull();
        assertThat(user.getPassword()).isNotEqualTo("senha12345");
        assertThat(passwordEncoder.matches("senha12345", user.getPassword())).isTrue();
    }

    @Test
    void signup_withDuplicateEmail_returns409() throws Exception {
        String body = """
                {
                  "companyName": "Locadora Silva",
                  "userName": "João Silva",
                  "email": "dup@locadorasilva.com",
                  "password": "senha12345"
                }
                """;

        mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void signup_withInvalidEmail_returns400() throws Exception {
        String body = """
                {
                  "companyName": "Locadora Silva",
                  "userName": "João Silva",
                  "email": "not-an-email",
                  "password": "senha12345"
                }
                """;

        mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signup_withBlankFields_returns400() throws Exception {
        String body = """
                {
                  "companyName": "",
                  "userName": "",
                  "email": "joao@locadorasilva.com",
                  "password": "senha12345"
                }
                """;

        mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signup_withShortPassword_returns400() throws Exception {
        String body = """
                {
                  "companyName": "Locadora Silva",
                  "userName": "João Silva",
                  "email": "short@locadorasilva.com",
                  "password": "abc"
                }
                """;

        mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
