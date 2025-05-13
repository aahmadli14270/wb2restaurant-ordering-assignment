package com.restaurant.ordering.Controller;

import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Service.QRCodeService;
import com.restaurant.ordering.Service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tables")
public class TableController {

    @Autowired
    private TableService tableService;

    @Autowired
    private QRCodeService qrCodeService;

    // Get all tables
    @GetMapping
    public List<TableItem> getAllTables() {
        List<TableItem> tables = tableService.getAllTables();
        if (tables.isEmpty()) {
            throw new NoSuchElementException("No tables found.");
        }
        return tables;
    }

    // Get a table by its ID
    @GetMapping("/{id}")
    public TableItem getTableById(@PathVariable Long id) {
        try {
            return tableService.getTableById(id);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Table with ID " + id + " not found.");
        }
    }

    // Add a new table
    @PostMapping
    public TableItem addTable(@RequestBody TableItem tableItem) {
        return tableService.addTable(tableItem);
    }

    // Delete a table
    @DeleteMapping("/{id}")
    public void deleteTable(@PathVariable Long id) {
        try {
            tableService.deleteTable(id);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Table with ID " + id + " not found.");
        }
    }

    // Update a table (for example, updating the table's status, number of seats, etc.)
    @PutMapping("/{id}")
    public TableItem updateTable(@PathVariable Long id, @RequestBody TableItem tableItem) {
        try {
            return tableService.updateTable(id, tableItem);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Table with ID " + id + " not found.");
        }
    }

//    // Create a table and generate QR code
//    @PostMapping
//    public TableItem createTable(@RequestBody TableItem tableItem) {
//        // Save the table (assuming save functionality exists in the service)
//        TableItem savedTable = tableService.createTable(tableItem);
//
//        // Optionally, you can also generate the QR code for the table here
//        String qrUrl = "https://localhost:8080/order?tableId=" + savedTable.getTableId();
//        BufferedImage qrImage = qrCodeService.generateQRCodeImage(qrUrl, 250, 250);
//
//        // For illustration, QR code is returned as part of the response (optional)
//        // You can decide to send it back or just store it for later use
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//            ImageIO.write(qrImage, "png", baos);
//            // Send the QR image as part of the response, or store in the DB to associate with the table.
//            savedTable.setQrCode(baos.toByteArray());
//        } catch (Exception e) {
//            // Handle error in QR code generation (you can add custom handling here)
//        }
//
//        return savedTable;
//    }
//
//    // Endpoint to get the QR code for a specific table
//    @GetMapping(value = "/{tableId}/qr", produces = MediaType.IMAGE_PNG_VALUE)
//    public ResponseEntity<byte[]> generateTableQRCode(@PathVariable Long tableId) {
//        String url = "https://localhost:8080/order?tableId=" + tableId;
//        BufferedImage qrImage = qrCodeService.generateQRCodeImage(url, 250, 250);
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//            ImageIO.write(qrImage, "png", baos);
//            return ResponseEntity.ok().body(baos.toByteArray());
//        } catch (Exception e) {
//            return ResponseEntity.status(500).build();
//        }
//    }
}
