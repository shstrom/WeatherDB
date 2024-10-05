package com.testprosjekt.WeatherDB.frost;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Station {
    String id;
    String name;
    String shortName;
    String coordinates;
    Double distance;
    ArrayList<String[]> values;

    boolean closest = false;

    public Station(String id, String name, String shortName) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.coordinates = null;
        this.distance = 1000.00;
        this.values = new ArrayList<String []>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getValues() {
        return values.toString();
    }


    public boolean isClosest() {
        return closest;
    }

    public void setClosest(boolean closest) {
        this.closest = closest;
    }

    public void calculateDistance(){

        this.coordinates = this.coordinates.replaceAll("['\\]]", "").replaceAll("\\[", "");
        String[] s = this.coordinates.split(",");

        double doubleX = Double.valueOf(s[0]);
        double doubleY = Double.valueOf(s[1]);
        double xDiff = Math.abs(doubleX - 10.7725);
        double yDiff = Math.abs(doubleY - 59.9022);
        this.setDistance(xDiff + yDiff);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Append basic fields
        sb.append("Station ID: ").append(id).append("\n");
        sb.append("Name: ").append(name).append("\n");
        sb.append("Short Name: ").append(shortName).append("\n");
        sb.append("Coordinates: ").append(coordinates).append("\n");
        sb.append("Distance: ").append(distance).append("\n");

        // Append the ArrayList<String[]> (values)
        sb.append("Values:\n");
        for (String[] array : values) {
            sb.append("  ").append(Arrays.toString(array)).append("\n");  // Format each String[] as a readable array
        }

        return sb.toString();
    }


    // Method to connect to SQLite database
    public static Connection connect() {
        // SQLite connection string (replace with the actual path to your SQLite file)
        String url = "jdbc:sqlite:weather.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // Create a table for storing the data (if not exists)
    public static void createNewTable() {
        String sql = "CREATE TABLE IF NOT EXISTS station_values ("
                + " id TEXT NOT NULL,"
                + " timestamp TEXT NOT NULL,"
                + " value REAL NOT NULL,"
                + " UNIQUE(id,timestamp)"
                + ");";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Method to insert data into the database
    public void insertIntoDatabase() {
        String sql = "INSERT INTO station_values(id, timestamp, value) VALUES(?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Loop through each String[] in the values ArrayList
            for (String[] entry : values) {
                String timestamp = entry[0]; // First element is the timestamp
                double value = Double.parseDouble(entry[1]); // Second element is the value

                // Set the values in the PreparedStatement
                pstmt.setString(1, this.id);
                pstmt.setString(2, timestamp);
                pstmt.setDouble(3, value);

                // Execute the insert
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


}
