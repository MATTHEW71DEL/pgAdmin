package org.matv.db;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DbManager {
    private static Map<String, Object> config;

    static {
        Yaml yaml = new Yaml();
        try (InputStream in = DbManager.class
                .getClassLoader()
                .getResourceAsStream("properties.yml")) {
            config = yaml.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final static String SELECT_ALL_CLIENTS = "SELECT phone, first_name, surname, patronymic FROM clients";
    private final static String CALL_INSERT_PROC = "CALL insert_client_proc(?, ?, ?, ?, ?)";

    private Connection getNewConnection() throws SQLException {
        Map<String, String> dbSettings = (Map<String, String>) config.get("db");

        return DriverManager.getConnection(
                dbSettings.get("url"),
                dbSettings.get("username"),
                dbSettings.get("password")
        );
    }

    public List<Client> readTableClients() throws SQLException {
        List<Client> clients = new LinkedList<>();
        try (Connection conn = getNewConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SELECT_ALL_CLIENTS)) {

            while (rs.next()) {
                clients.add(new Client(
                        rs.getString("phone"),
                        rs.getString("first_name"),
                        rs.getString("surname"),
                        rs.getString("patronymic")));
            }
        }
        return clients;
    }

    public void syncChanges(List<Client> newClients) throws SQLException {
        if (newClients.isEmpty()) return;

        try (Connection conn = getNewConnection()) {
            conn.setAutoCommit(false);

            try (CallableStatement cstmt = conn.prepareCall(CALL_INSERT_PROC)) {
                for (Client client : newClients) {
                    cstmt.setString(1, client.getPhone());
                    cstmt.setString(2, client.getName());
                    cstmt.setString(3, client.getSurname());
                    cstmt.setString(4, client.getPatronymic());
                    cstmt.registerOutParameter(5, Types.INTEGER);
                    cstmt.execute();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}