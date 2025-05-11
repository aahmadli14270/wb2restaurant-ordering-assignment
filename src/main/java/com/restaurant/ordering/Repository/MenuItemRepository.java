package com.restaurant.ordering.Repository;

import com.restaurant.ordering.Model.MenuItem;
import com.restaurant.ordering.Enums.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    // Find a menu item by its ID
    Optional<MenuItem> findById(Long id);

    // Find all menu items by category (e.g., APPETIZER, MAIN_COURSE)
    List<MenuItem> findByCategory(MenuCategory category);

    // Find all menu items by name (e.g., search for a specific item)
    List<MenuItem> findByNameContainingIgnoreCase(String name);

    // Find all menu items ordered by price (ascending)
    List<MenuItem> findAllByOrderByPriceAsc();

    // Find all menu items ordered by price (descending)
    List<MenuItem> findAllByOrderByPriceDesc();
}