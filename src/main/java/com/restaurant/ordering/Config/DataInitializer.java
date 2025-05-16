package com.restaurant.ordering.Config;

import com.restaurant.ordering.Enums.MenuCategory;
import com.restaurant.ordering.Model.MenuItem;
import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Model.Users.KitchenStaff;
import com.restaurant.ordering.Model.Users.Manager;
import com.restaurant.ordering.Model.Users.User;
import com.restaurant.ordering.Model.Users.Waiter;
import com.restaurant.ordering.Repository.MenuItemRepository;
import com.restaurant.ordering.Repository.TableItemRepository;
import com.restaurant.ordering.Repository.UserRepository;
import com.restaurant.ordering.Enums.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean(name = "customDataInitializer")  // Renaming the bean to avoid conflict
    public CommandLineRunner dataInitializer(
            UserRepository userRepository, 
            MenuItemRepository menuItemRepository,
            TableItemRepository tableItemRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Initialize users
            initializeUsers(userRepository, passwordEncoder);

            // Initialize menu items
            initializeMenuItems(menuItemRepository);

            // Initialize tables
            initializeTables(tableItemRepository);
        };
    }

    private void initializeUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        // Create Manager if it doesn't exist
        if (!userRepository.existsByUsername("manager")) {
            Manager manager = new Manager();
            manager.setUsername("manager");
            manager.setPasswordHash(passwordEncoder.encode("1234"));  // Use PasswordEncoder to encode passwords
            manager.setRole(UserRole.MANAGER);
            userRepository.save(manager);
        }

        // Create Waiter if it doesn't exist
        if (!userRepository.existsByUsername("waiter")) {
            Waiter waiter = new Waiter();
            waiter.setUsername("waiter");
            waiter.setPasswordHash(passwordEncoder.encode("1234"));
            waiter.setRole(UserRole.WAITER);
            userRepository.save(waiter);
        }

        // Create KitchenStaff if it doesn't exist
        if (!userRepository.existsByUsername("kitchen")) {
            KitchenStaff kitchenStaff = new KitchenStaff();
            kitchenStaff.setUsername("kitchen");
            kitchenStaff.setPasswordHash(passwordEncoder.encode("1234"));
            kitchenStaff.setRole(UserRole.KITCHEN);
            userRepository.save(kitchenStaff);
        }
    }

    private void initializeMenuItems(MenuItemRepository menuItemRepository) {
        // Check if menu items already exist
        if (menuItemRepository.count() > 0) {
            return; // Skip initialization if menu items already exist
        }

        // Create appetizers
        MenuItem bruschetta = MenuItem.builder()
                .name("Bruschetta")
                .description("Toasted bread topped with tomatoes, garlic, and basil")
                .price(8.99)
                .category(MenuCategory.APPETIZER)
                .build();
        menuItemRepository.save(bruschetta);

        MenuItem calamari = MenuItem.builder()
                .name("Calamari")
                .description("Fried squid served with marinara sauce")
                .price(12.99)
                .category(MenuCategory.APPETIZER)
                .build();
        menuItemRepository.save(calamari);

        // Create main courses
        MenuItem steak = MenuItem.builder()
                .name("Ribeye Steak")
                .description("12oz ribeye steak with mashed potatoes and vegetables")
                .price(29.99)
                .category(MenuCategory.MAIN_COURSE)
                .build();
        menuItemRepository.save(steak);

        MenuItem salmon = MenuItem.builder()
                .name("Grilled Salmon")
                .description("Fresh salmon fillet with rice and asparagus")
                .price(24.99)
                .category(MenuCategory.MAIN_COURSE)
                .build();
        menuItemRepository.save(salmon);

        MenuItem pasta = MenuItem.builder()
                .name("Spaghetti Carbonara")
                .description("Spaghetti with creamy sauce, bacon, and parmesan")
                .price(18.99)
                .category(MenuCategory.MAIN_COURSE)
                .build();
        menuItemRepository.save(pasta);

        // Create desserts
        MenuItem tiramisu = MenuItem.builder()
                .name("Tiramisu")
                .description("Classic Italian dessert with coffee-soaked ladyfingers")
                .price(9.99)
                .category(MenuCategory.DESSERT)
                .build();
        menuItemRepository.save(tiramisu);

        MenuItem cheesecake = MenuItem.builder()
                .name("New York Cheesecake")
                .description("Creamy cheesecake with graham cracker crust")
                .price(8.99)
                .category(MenuCategory.DESSERT)
                .build();
        menuItemRepository.save(cheesecake);

        // Create drinks
        MenuItem wine = MenuItem.builder()
                .name("House Red Wine")
                .description("Glass of house red wine")
                .price(7.99)
                .category(MenuCategory.DRINK)
                .build();
        menuItemRepository.save(wine);

        MenuItem soda = MenuItem.builder()
                .name("Soft Drink")
                .description("Coke, Diet Coke, Sprite, or Fanta")
                .price(2.99)
                .category(MenuCategory.DRINK)
                .build();
        menuItemRepository.save(soda);

        MenuItem coffee = MenuItem.builder()
                .name("Coffee")
                .description("Regular or decaf coffee")
                .price(3.99)
                .category(MenuCategory.DRINK)
                .build();
        menuItemRepository.save(coffee);
    }

    private void initializeTables(TableItemRepository tableItemRepository) {
        // Check if tables already exist
        if (tableItemRepository.count() > 0) {
            return; // Skip initialization if tables already exist
        }

        // Create 10 tables
        for (int i = 1; i <= 10; i++) {
            TableItem table = TableItem.builder()
                    .tableId((long) i)
                    .build();
            tableItemRepository.save(table);
        }
    }
}
