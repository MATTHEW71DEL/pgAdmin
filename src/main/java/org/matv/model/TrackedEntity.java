package org.matv.model;

import lombok.Getter;
import lombok.Setter;

public class TrackedEntity<T> {
    @Getter @Setter
    private T entity;

    private final T originalEntity;

    @Getter @Setter
    private RowState state;

    @SuppressWarnings("unchecked")
    public TrackedEntity(T entity, RowState state) {
        this.entity = entity;
        this.state = state;

        if (entity instanceof Product) {
            this.originalEntity = (T) new Product((Product) entity);
        } else if (entity instanceof Category) {
            this.originalEntity = (T) new Category((Category) entity);
        } else {
            this.originalEntity = null;
        }
    }

    public void rejectChanges() {
        if (state == RowState.MODIFIED || state == RowState.DELETED) {
            if (entity instanceof Product) {
                copyProductData((Product) originalEntity, (Product) entity);
            } else if (entity instanceof Category) {
                copyCategoryData((Category) originalEntity, (Category) entity);
            }
            this.state = RowState.UNCHANGED;
        }
    }

    private void copyProductData(Product source, Product target) {
        target.setCategoryId(source.getCategoryId());
        target.setName(source.getName());
        target.setPrice(source.getPrice());
        target.setStockQuantity(source.getStockQuantity());
    }

    private void copyCategoryData(Category source, Category target) {
        target.setName(source.getName());
        target.setDescription(source.getDescription());
    }
}