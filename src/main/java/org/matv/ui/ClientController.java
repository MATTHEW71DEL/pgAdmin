package org.matv.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.matv.db.Client;
import org.matv.db.Repository;

import java.sql.SQLException;

public class ClientController {
    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, String> colSurname, colName, colPatronymic, colPhone;

    @FXML private TextField txtSurname, txtName, txtPatronymic, txtPhone;

    private final Repository repository = new Repository();

    @FXML
    public void initialize() {
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPatronymic.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        clientTable.setItems(repository.getAllClients());
    }

    @FXML
    private void handleAddLocal() {
        Client c = new Client(txtPhone.getText(), txtName.getText(), txtSurname.getText(), txtPatronymic.getText());
        repository.addLocally(c);

        txtPhone.clear(); txtName.clear(); txtSurname.clear(); txtPatronymic.clear();
        showInfo("Локальное добавление", "Запись добавлена в память. Не забудьте сохранить в БД!");
    }

    @FXML
    private void handleSaveToDb() {
        try {
            repository.saveChanges();
            showInfo("Успех", "Данные сохранены в БД");
        } catch (SQLException e) {
            showError("Ошибка", e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        try {
            repository.loadFromDb();
        } catch (SQLException e) {
            showError("Ошибка", e.getMessage());
        }
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