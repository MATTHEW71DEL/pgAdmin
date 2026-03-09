package org.matv.repo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.matv.db.DbManager;
import org.matv.model.Category;
import org.matv.model.Product;
import org.matv.model.RowState;
import org.matv.model.TrackedEntity;

import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Collectors;

public class Repository {
    private final DbManager dbManager = new DbManager();

    @Getter
    private final ObservableList<TrackedEntity<Product>> productStates = FXCollections.observableArrayList();

    @Getter
    private final ObservableList<TrackedEntity<Category>> categoryStates = FXCollections.observableArrayList();

    public void loadFromDb() throws SQLException {
        categoryStates.setAll(dbManager.readTableCategories().stream()
                .map(c -> new TrackedEntity<>(c, RowState.UNCHANGED))
                .collect(Collectors.toList()));

        productStates.setAll(dbManager.readTableProducts().stream()
                .map(p -> new TrackedEntity<>(p, RowState.UNCHANGED))
                .collect(Collectors.toList()));
    }

    public void saveChanges() throws SQLException {
        dbManager.syncAll(productStates, categoryStates);
        loadFromDb();
    }

    public Optional<Category> getCategoryForProduct(int categoryId) {
        return categoryStates.stream()
                .filter(cs -> cs.getState() != RowState.DELETED)
                .map(TrackedEntity::getEntity)
                .filter(c -> c.getId() == categoryId)
                .findFirst();
    }

    public void addProductLocally(Product product) {
        productStates.add(new TrackedEntity<>(product, RowState.ADDED));
    }

    public void markProductDeleted(TrackedEntity<Product> state) {
        if (state.getState() == RowState.ADDED) {
            productStates.remove(state);
        } else {
            state.setState(RowState.DELETED);
        }
    }

    public void rejectProductChanges(TrackedEntity<Product> state) {
        if (state.getState() == RowState.ADDED) {
            productStates.remove(state);
        } else {
            state.rejectChanges();
        }
    }
}