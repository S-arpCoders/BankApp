package com.Scoders.BankingApp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CsrfTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void testRegisterPageAccessible() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void testLoginPageAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void testPostRegisterWithoutCsrfToken_ShouldBeForbidden() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "testUser")
                        .param("surname", "testSurname")
                        .param("password", "testPassword123")
                        .param("confirmPassword", "testPassword123"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testPostLoginWithoutCsrfToken_ShouldBeForbidden() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "testUser")
                        .param("password", "testPassword123"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testPostRegisterWithCsrfToken_ShouldBeAccepted() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "testUser" + System.currentTimeMillis())
                        .param("surname", "testSurname")
                        .param("password", "TestP@ssw0rd7x9")
                        .param("confirmPassword", "TestP@ssw0rd7x9")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void testPostLoginWithCsrfToken_ShouldBeAccepted() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "nonexistentUser")
                        .param("password", "wrongPassword")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void testMultiplePostRequestsWithoutCsrfToken_AllForbidden() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "user1")
                        .param("surname", "surname1")
                        .param("password", "pass1")
                        .param("confirmPassword", "pass1"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/login")
                        .param("username", "user1")
                        .param("password", "pass1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testPostWithInvalidCsrfToken_ShouldBeForbidden() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "testUser")
                        .param("surname", "testSurname")
                        .param("password", "testPassword123")
                        .param("confirmPassword", "testPassword123")
                        .header("X-XSRF-TOKEN", "invalid-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testPasswordMismatchWithCsrfToken_ShouldReturnError() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "testUser" + System.currentTimeMillis())
                        .param("surname", "testSurname")
                        .param("password", "TestP@ssw0rd7x9")
                        .param("confirmPassword", "TestP@ssw0rd8x7")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("response", "Passwords do not match!"));
    }
}
