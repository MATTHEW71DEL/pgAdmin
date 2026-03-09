package org.matv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private int id;
    private String name;
    private String description;

    public Category(Category other) {
        this.id = other.id;
        this.name = other.name;
        this.description = other.description;
    }
}