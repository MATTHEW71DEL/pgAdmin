package org.matv.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.matv.model.Category;
import org.matv.model.Product;
import org.matv.model.RowState;
import org.matv.model.TrackedEntity;
import org.matv.repo.Repository;

import java.sql.SQLException;
import java.util.Optional;

public class ProductController {
    @FXML private TableView<TrackedEntity<Product>> productTable;
    @FXML private TableColumn<TrackedEntity<Product>, String> colName, colPrice, colStock, colCategoryId, colState;
    @FXML private TextField txtName, txtPrice, txtStock, txtCategoryId;
    @FXML private ListView<String> hierarchyListView;

    private final Repository repository = new Repository();

    @FXML
    public void initialize() {
        // Настройка колонок (Product)
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEntity().getName()));
        colPrice.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getEntity().getPrice())));
        colStock.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getEntity().getStockQuantity())));
        colCategoryId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getEntity().getCategoryId())));
        colState.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getState().toString()));

        productTable.setItems(repository.getProductStates());

        // Слушатель выбора строки
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillFields(newVal.getEntity());
            }
        });
    }

    @FXML
    private void handleRefresh() {
        try {
            repository.loadFromDb();
            showInfo("Успех", "Данные загружены из базы");
        } catch (SQLException e) {
            showError("Ошибка БД", e.getMessage());
        }
    }

    @FXML
    private void handleAddLocal() {
        try {
            Product p = new Product(
                    0, // ID присвоит БД
                    Integer.parseInt(txtCategoryId.getText()),
                    txtName.getText(),
                    Double.parseDouble(txtPrice.getText()),
                    Integer.parseInt(txtStock.getText())
            );
            repository.addProductLocally(p);
            clearFields();
        } catch (NumberFormatException e) {
            showError("Ошибка ввода", "Проверьте числовые поля (Цена, Склад, ID категории)");
        }
    }

    @FXML
    private void handleEditLocal() {
        TrackedEntity<Product> selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Product p = selected.getEntity();
            p.setName(txtName.getText());
            p.setPrice(Double.parseDouble(txtPrice.getText()));
            p.setStockQuantity(Integer.parseInt(txtStock.getText()));
            p.setCategoryId(Integer.parseInt(txtCategoryId.getText()));

            if (selected.getState() == RowState.UNCHANGED) {
                selected.setState(RowState.MODIFIED);
            }
            productTable.refresh();
        }
    }

    @FXML
    private void handleDeleteLocal() {
        TrackedEntity<Product> selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            repository.markProductDeleted(selected);
            productTable.refresh();
        }
    }

    @FXML
    private void handleCancelEdit() {
        TrackedEntity<Product> selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            repository.rejectProductChanges(selected);
            productTable.refresh();
            fillFields(selected.getEntity());
        }
    }

    @FXML
    private void handleShowHierarchy() {
        ObservableList<String> items = FXCollections.observableArrayList();

        for (TrackedEntity<Product> pState : repository.getProductStates()) {
            if (pState.getState() == RowState.DELETED) continue;

            Product p = pState.getEntity();
            items.add("ТОВАР: " + p.getName() + " [Цена: " + p.getPrice() + "]");

            // Ищем категорию для этого товара
            Optional<Category> categoryOpt = repository.getCategoryForProduct(p.getCategoryId());

            if (categoryOpt.isPresent()) {
                Category c = categoryOpt.get();
                items.add("   └─ Категория: " + c.getName());
                items.add("   └─ Описание: " + c.getDescription());
            } else {
                items.add("   └─ (Категория ID " + p.getCategoryId() + " не найдена)");
            }
            items.add(""); // Пустая строка для разделителя
        }
        hierarchyListView.setItems(items);
    }

    @FXML
    private void handleSaveToDb() {
        try {
            repository.saveChanges();
            productTable.refresh();
            showInfo("Успех", "Все изменения сохранены в БД");
        } catch (SQLException e) {
            showError("Ошибка сохранения", e.getMessage());
        }
    }

    private void fillFields(Product p) {
        txtName.setText(p.getName());
        txtPrice.setText(String.valueOf(p.getPrice()));
        txtStock.setText(String.valueOf(p.getStockQuantity()));
        txtCategoryId.setText(String.valueOf(p.getCategoryId()));
    }

    private void clearFields() {
        txtName.clear(); txtPrice.clear(); txtStock.clear(); txtCategoryId.clear();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}