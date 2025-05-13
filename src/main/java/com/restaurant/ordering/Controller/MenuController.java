package com.restaurant.ordering.Controller;

import com.restaurant.ordering.Model.MenuItem;
import com.restaurant.ordering.Service.MenuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/manager/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping
    public List<MenuItem> getAllItems() {
        List<MenuItem> items = menuService.getAllMenuItems();
        if (items.isEmpty()) {
            throw new NoSuchElementException("No menu items found.");
        }
        return items;
    }

    @PostMapping
    public MenuItem addMenuItem(@RequestBody MenuItem item) {
        if (item.getName() == null) {
            throw new IllegalArgumentException("Menu item name must not be null.");
        }
        return menuService.addMenuItem(item);
    }

    @PutMapping("/{id}")
    public MenuItem updateItem(@PathVariable Long id, @RequestBody MenuItem item) {
        try {
            return menuService.updateMenuItem(id, item);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Menu item with ID " + id + " not found.");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        try {
            menuService.deleteMenuItem(id);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Menu item with ID " + id + " not found.");
        }
    }
}