package com.restaurant.ordering.Controller;

import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tables")
public class TableController {

    @Autowired
    private TableService tableService;

//    @Autowired
//    private QRCodeService qrCodeService;

    // Get all tables
    @GetMapping
    public List<TableItem> getAllTables() {
        return tableService.getAllTables();
    }

    // Get a table by its ID
    @GetMapping("/{id}")
    public TableItem getTableById(@PathVariable Long id) {
        return tableService.getTableById(id);
    }

    // Add a new table
    @PostMapping
    public TableItem addTable(@RequestBody TableItem tableItem) {
        return tableService.addTable(tableItem);
    }

    // Delete a table
    @DeleteMapping("/{id}")
    public void deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
    }

    // Update a table (for example, updating the table's status, number of seats, etc.)
    @PutMapping("/{id}")
    public TableItem updateTable(@PathVariable Long id, @RequestBody TableItem tableItem) {
        return tableService.updateTable(id, tableItem);
    }

    //    // Generate a QR code for a table (this would typically return a URL or base64 encoded string of the QR code)
//    @GetMapping("/{id}/qr")
//    public String generateQRCodeForTable(@PathVariable Long id) {
//        // Assuming generateQRCode method generates the QR code for a table by its ID
//        return qrCodeService.generateQRCodeForTable(id);
//    }
}
