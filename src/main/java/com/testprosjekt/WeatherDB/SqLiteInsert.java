/*
package com.testprosjekt.WeatherDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqLiteInsert {

    public static Connection connect() {

        var url = "jdbc:sqlite:weather.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createNewTable() {
        String sql = "CREATE TABLE IF NOT EXISTS values_table ("
                + " id INTEGER PRIMARY KEY,"
                + " timestamp TEXT NOT NULL,"
                + " value REAL NOT NULL"
                + ");";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insert(String timestamp, double value) {
        String sql = "INSERT INTO values_table(timestamp, value) VALUES(?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, timestamp);
            pstmt.setDouble(2, value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
*/
