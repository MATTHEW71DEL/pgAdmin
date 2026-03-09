package org.matv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private int id;
    private int categoryId;
    private String name;
    private double price;
    private int stockQuantity;

    public Product(Product other) {
        this.id = other.id;
        this.categoryId = other.categoryId;
        this.name = other.name;
        this.price = other.price;
        this.stockQuantity = other.stockQuantity;
    }
}