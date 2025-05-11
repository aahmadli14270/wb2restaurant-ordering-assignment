package com.restaurant.ordering.Model;

import com.restaurant.ordering.Model.Users.Customer;
import jakarta.persistence.*;
import jakarta.persistence.Table;

import lombok.*;

import java.util.List;

@Entity
@Table(name = "tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tableId;  // Unique identifier for the table, if applicable.

    // One table can be associated with one customer (or null if no customer is assigned).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    // One table can have multiple orders.
    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL)
    private List<Order> orders;
}
