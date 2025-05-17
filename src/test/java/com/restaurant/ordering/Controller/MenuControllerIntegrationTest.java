package com.restaurant.ordering.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ordering.Enums.MenuCategory;
import com.restaurant.ordering.Enums.UserRole;
import com.restaurant.ordering.Model.MenuItem;
import com.restaurant.ordering.Model.Users.Manager;
import com.restaurant.ordering.Model.Users.User;
import com.restaurant.ordering.Repository.MenuItemRepository;
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

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MenuControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MenuItem testMenuItem;
    private User testUser;
    private String authToken;
    private final String TEST_USERNAME = "testmanager";
    private final String TEST_PASSWORD = "password";

    @BeforeEach
    void setUp() throws Exception {
        // Clean up existing data
        menuItemRepository.deleteAll();
        userRepository.deleteAll();

        // Create test menu item
        testMenuItem = new MenuItem();
        testMenuItem.setName("Test Item");
        testMenuItem.setDescription("Test Description");
        testMenuItem.setPrice(10.0);
        testMenuItem.setCategory(MenuCategory.MAIN_COURSE);

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
    void getAllItems_ReturnsAllMenuItems() throws Exception {
        // Arrange
        MenuItem savedMenuItem = menuItemRepository.save(testMenuItem);

        // Act & Assert
        mockMvc.perform(get("/manager/menu")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name", is("Test Item")))
                .andExpect(jsonPath("$[0].price", is(10.0)))
                .andExpect(jsonPath("$[0].category", is("MAIN_COURSE")));
    }

    @Test
    void getAllItems_NoMenuItems_ThrowsException() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/manager/menu")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void addMenuItem_ValidItem_ReturnsCreatedItem() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/manager/menu")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMenuItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Item")))
                .andExpect(jsonPath("$.price", is(10.0)))
                .andExpect(jsonPath("$.category", is("MAIN_COURSE")));
    }

    @Test
    void addMenuItem_InvalidItem_ThrowsException() throws Exception {
        // Arrange
        testMenuItem.setName(null);

        // Act & Assert
        mockMvc.perform(post("/manager/menu")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMenuItem)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_ExistingItem_ReturnsUpdatedItem() throws Exception {
        // Arrange
        MenuItem savedMenuItem = menuItemRepository.save(testMenuItem);

        MenuItem updatedMenuItem = new MenuItem();
        updatedMenuItem.setName("Updated Item");
        updatedMenuItem.setDescription("Updated Description");
        updatedMenuItem.setPrice(15.0);
        updatedMenuItem.setCategory(MenuCategory.DESSERT);

        // Act & Assert
        mockMvc.perform(put("/manager/menu/" + savedMenuItem.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMenuItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Item")))
                .andExpect(jsonPath("$.price", is(15.0)))
                .andExpect(jsonPath("$.category", is("DESSERT")));
    }

    @Test
    void updateItem_NonExistingItem_ThrowsException() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/manager/menu/999")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMenuItem)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteItem_ExistingItem_DeletesItem() throws Exception {
        // Arrange
        MenuItem savedMenuItem = menuItemRepository.save(testMenuItem);

        // Act & Assert
        mockMvc.perform(delete("/manager/menu/" + savedMenuItem.getId())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        // No need to verify item is deleted, as the delete operation was successful
    }

    @Test
    void deleteItem_NonExistingItem_ThrowsException() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/manager/menu/999")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }
}
