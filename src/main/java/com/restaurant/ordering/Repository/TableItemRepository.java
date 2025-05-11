package com.restaurant.ordering.Repository;

import com.restaurant.ordering.Model.TableItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableItemRepository extends JpaRepository<TableItem, Long> {

    // Find a table by its unique ID
    Optional<TableItem> findById(Long id);

    // Find a table by its unique table identifier (e.g., table number or QR code id)
    Optional<TableItem> findByTableId(Long tableId);

    // Optional: Find all tables (if needed for specific use cases, like displaying all available tables)
    List<TableItem> findAll();
}