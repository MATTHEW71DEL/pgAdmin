package org.matv.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.matv.db.DbManager;
import org.matv.model.Client;

import java.sql.SQLException;

public class ClientController {
    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, String> colSurname, colName, colPatronymic, colPhone;
    @FXML private TextField txtSurname, txtName, txtPatronymic, txtPhone;
    @FXML private Label lblCount;

    private final DbManager dbManager = new DbManager();

    @FXML
    public void initialize() {
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPatronymic.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        handleRefresh();
    }

    @FXML
    private void handleRefresh() {
        try {
            ObservableList<Client> clients = FXCollections.observableArrayList(dbManager.readTableClients());
            clientTable.setItems(clients);

            int count = dbManager.getClientsCount();
            lblCount.setText("Общее число клиентов: " + count);
        } catch (SQLException e) {
            showError("Ошибка загрузки", e.getMessage());
        }
    }

    @FXML
    private void handleAddClient() {
        Client newClient = new Client(
                txtPhone.getText(),
                txtName.getText(),
                txtSurname.getText(),
                txtPatronymic.getText()
        );

        try {
            int newId = dbManager.addClientViaProcedure(newClient);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Успех");
            alert.setHeaderText(null);
            alert.setContentText("Клиент добавлен! Присвоен ID: " + newId);
            alert.showAndWait();

            handleRefresh();
            clearFields();
        } catch (SQLException e) {
            showError("Ошибка добавления", e.getMessage());
        }
    }

    private void clearFields() {
        txtPhone.clear(); txtName.clear(); txtSurname.clear(); txtPatronymic.clear();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}