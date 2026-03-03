package org.matv.db;

import org.matv.model.Client;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DbManager {
    private final static String URL = "jdbc:postgresql://localhost:5432/ShopDB";
    private final static String USER = "postgres";
    private final static String PASSWORD = "2006ukt,";

    private final static String SELECT_ALL_CLIENTS = "SELECT * FROM clients";
    private final static String COUNT_CLIENTS = "SELECT COUNT(*) FROM clients";
    private final static String CALL_INSERT_PROC = "CALL insert_client_proc(?, ?, ?, ?, ?)";

    private Connection getNewConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
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

    public int getClientsCount() throws SQLException {
        try (Connection conn = getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(COUNT_CLIENTS)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    public int addClientViaProcedure(Client client) throws SQLException {
        try (Connection conn = getNewConnection();
             CallableStatement cstmt = conn.prepareCall(CALL_INSERT_PROC)) {

            cstmt.setString(1, client.getPhone());
            cstmt.setString(2, client.getName());
            cstmt.setString(3, client.getSurname());
            cstmt.setString(4, client.getPatronymic());

            cstmt.registerOutParameter(5, Types.INTEGER);
            cstmt.setNull(5, Types.INTEGER);

            cstmt.execute();

            return cstmt.getInt(5);
        }
    }
}