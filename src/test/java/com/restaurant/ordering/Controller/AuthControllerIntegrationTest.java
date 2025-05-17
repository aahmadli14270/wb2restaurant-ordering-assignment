package com.restaurant.ordering.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ordering.Enums.UserRole;
import com.restaurant.ordering.Model.Users.KitchenStaff;
import com.restaurant.ordering.Model.Users.User;
import com.restaurant.ordering.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private final String TEST_USERNAME = "testuser";
    private final String TEST_PASSWORD = "password";

    @BeforeEach
    void setUp() {
        // Clean up existing users
        userRepository.deleteAll();

        // Create test user (KitchenStaff)
        testUser = new KitchenStaff(TEST_USERNAME, passwordEncoder.encode(TEST_PASSWORD));
        testUser.setRole(UserRole.KITCHEN);
        testUser = userRepository.save(testUser);
    }

    @Test
    void login_ValidCredentials_ReturnsToken() throws Exception {
        // Arrange
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", TEST_USERNAME);
        credentials.put("password", TEST_PASSWORD);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    void login_InvalidUsername_ReturnsBadRequest() throws Exception {
        // Arrange
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "wronguser");
        credentials.put("password", TEST_PASSWORD);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_InvalidPassword_ReturnsBadRequest() throws Exception {
        // Arrange
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", TEST_USERNAME);
        credentials.put("password", "wrongpassword");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_ValidData_ReturnsSuccess() throws Exception {
        // Arrange
        Map<String, String> registrationData = new HashMap<>();
        registrationData.put("username", "newuser");
        registrationData.put("password", "password123");
        registrationData.put("role", "WAITER");

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User registered successfully")))
                .andExpect(jsonPath("$.username", is("newuser")))
                .andExpect(jsonPath("$.role", is("WAITER")));

        // Verify user was created in the database
        assertTrue(userRepository.existsByUsername("newuser"));
    }

    @Test
    void register_MissingData_ReturnsBadRequest() throws Exception {
        // Arrange
        Map<String, String> registrationData = new HashMap<>();
        registrationData.put("username", "newuser");
        // Missing password and role

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Username, password, and role are required")));
    }

    @Test
    void register_ExistingUsername_ReturnsBadRequest() throws Exception {
        // Arrange
        Map<String, String> registrationData = new HashMap<>();
        registrationData.put("username", TEST_USERNAME); // Using existing username
        registrationData.put("password", "password123");
        registrationData.put("role", "MANAGER");

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Username already exists")));
    }

    @Test
    void register_InvalidRole_ReturnsBadRequest() throws Exception {
        // Arrange
        Map<String, String> registrationData = new HashMap<>();
        registrationData.put("username", "newuser");
        registrationData.put("password", "password123");
        registrationData.put("role", "INVALID_ROLE");

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
