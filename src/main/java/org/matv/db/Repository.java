package org.matv.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.sql.SQLException;

public class Repository {
    private final DbManager dbManager = new DbManager();

    @Getter
    private final ObservableList<Client> allClients = FXCollections.observableArrayList();

    private final ObservableList<Client> newClients = FXCollections.observableArrayList();

    public void loadFromDb() throws SQLException {
        allClients.setAll(dbManager.readTableClients());
        newClients.clear();
    }

    public void addLocally(Client client) {
        allClients.add(client);
        newClients.add(client);
    }

    public void saveChanges() throws SQLException {
        if (!newClients.isEmpty()) {
            dbManager.syncChanges(newClients);
            newClients.clear();
        }
    }
}