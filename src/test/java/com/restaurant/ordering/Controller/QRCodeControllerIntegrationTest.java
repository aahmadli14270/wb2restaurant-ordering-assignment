package com.restaurant.ordering.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ordering.Enums.UserRole;
import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Model.Users.Manager;
import com.restaurant.ordering.Model.Users.User;
import com.restaurant.ordering.Repository.TableItemRepository;
import com.restaurant.ordering.Repository.UserRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class QRCodeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TableItemRepository tableItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private TableItem testTable;
    private User testUser;
    private String authToken;
    private final String TEST_USERNAME = "testmanager";
    private final String TEST_PASSWORD = "password";

    @BeforeEach
    void setUp() throws Exception {
        // Clean up existing data
        tableItemRepository.deleteAll();
        userRepository.deleteAll();

        // Create test table
        testTable = new TableItem();
        testTable.setTableId(101L);
        testTable.setOrders(new ArrayList<>());
        testTable = tableItemRepository.save(testTable);

        // Create test manager user
        testUser = new Manager(TEST_USERNAME, passwordEncoder.encode(TEST_PASSWORD));
        testUser.setRole(UserRole.MANAGER);
        testUser = userRepository.save(testUser);

        // Get JWT token
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", TEST_USERNAME);
        credentials.put("password", TEST_PASSWORD);

        String response = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONObject jsonResponse = new JSONObject(response);
        authToken = jsonResponse.getString("token");
    }

    @Test
    void generateQRCode_ValidTableId_ReturnsPngImage() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/qr/generate/" + testTable.getTableId())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(header().string("Content-Type", "image/png"));
    }

    @Test
    void generateQRCode_InvalidTableId_StillReturnsImage() throws Exception {
        // Even with an invalid table ID, the controller should still generate a QR code
        // since it doesn't validate if the table exists
        mockMvc.perform(get("/api/qr/generate/999")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(header().string("Content-Type", "image/png"));
    }
}
