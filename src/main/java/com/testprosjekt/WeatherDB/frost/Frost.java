package com.testprosjekt.WeatherDB.frost;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class Frost {

    public static void main(String[] args) {

        Dotenv cId = Dotenv.load();

        ArrayList<Station> stations = new ArrayList<>();
        String stationId = "";
        try {
            String url = "https://frost.met.no/sources/v0.jsonld?elements=air_temperature&municipality=Oslo";
            // Replace spaces
            url = url.replaceAll(" ", "%20");
            // Issue an HTTP GET request
            URL Url = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) Url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64((cId.get("CLIENT_ID") + ":").getBytes("UTF-8"))));
            // Extract JSON data
            JSONObject object = new JSONObject(new JSONTokener(new InputStreamReader(conn.getInputStream())));
            JSONArray data = object.getJSONArray("data");
            //JSONArray geometry;
            // Loop through the data
            for (int i = 0; i < data.length(); i++) {
                object = data.getJSONObject(i);

                Station station = new Station(object.getString("id"), object.getString("name"), object.getString("shortName"));

                JSONObject g = object.getJSONObject("geometry");
                var c = g.get("coordinates");
                String cs = String.format("%s", c);
                station.setCoordinates(cs);
                station.calculateDistance();

                if (!stations.contains(station))stations.add (station);
                stationId = stationId + object.getString("id") + ",";


            }
        } catch (Exception ex) {
            System.out.println("Error: the data retrieval was not successful!");
            ex.printStackTrace();
        }

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);
        String dateRange = sevenDaysAgo + "/" + today;


        try {
            // Insert your own client ID
            String client_id = "cfb662ba-dfac-456a-b854-88fefbf51a9e";
            // Build the URL and define parameters
            String url = "https://frost.met.no/observations/v0.jsonld?";
            url += "sources=" + stationId;
            url += "&elements=" + "mean(air_temperature P1D)";//bedre kode senere?
            url += "&referencetime=" + dateRange;
            url += "&levels=default";
            url += "&timeoffsets=default";
            // Replace spaces
            url = url.replaceAll(" ", "%20");
            // Issue an HTTP GET request
            URL Url = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) Url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64((client_id + ":").getBytes("UTF-8"))));
            // Extract JSON data
            JSONObject object = new JSONObject(new JSONTokener(new InputStreamReader(conn.getInputStream())));
            JSONArray data = object.getJSONArray("data");
            JSONArray observations;
            // Loop through the data
            for (int i = 0; i < data.length(); i++) {
                object = data.getJSONObject(i);
                String x = object.getString("referenceTime");
                String compId = object.getString("sourceId").substring(0, 7);
                observations = object.getJSONArray("observations");
                for (Station s : stations){
                    if (compId.equals(s.getId())){
                        String y = "";
                        for (int j = 0; j < observations.length(); j++) {
                            JSONObject objectO = observations.getJSONObject(j);
                            var v = objectO.getBigDecimal("value");
                            y = String.format("%s", v);
                            String [] temp = {x, y};
                            s.values.add(temp);
                        }
                        //dårlig optimalisering eller?
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Error: the data retrieval was not successful!");
            ex.printStackTrace();
        }



        //finne nærmeste stasjon

        double comp = 3.00;

        for (Station s: stations){
            if (s.distance < comp){
                comp = s.distance;
            }
        }
        for (Station s: stations){
            if (s.distance == comp){
                s.setClosest(true);
                System.out.println(s.shortName + " is closest to Gamlebyen!");
            } else {
                s.setClosest(false);
            }
        }

        //clearTable();
        Station.createNewTable();

        for (Station s : stations){
            //System.out.println(s.getValues());
            //System.out.println(s.toString());
            if (s.closest){
                System.out.println(s);
                s.insertIntoDatabase();
                displayDataFromDatabase();

            }
        }


    }
    public static void displayDataFromDatabase() {
        String sql = "SELECT id, timestamp, value FROM station_values ORDER BY timestamp ASC";

        try (Connection conn = Station.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Loop through the result set and print the results
            while (rs.next()) {
                System.out.println(
                        "ID: " + rs.getString("id") +
                                ", Timestamp: " + rs.getString("timestamp") +
                                ", Value: " + rs.getDouble("value"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void clearTable() {
        String sql = "DELETE FROM station_values";  // This clears the "station_values" table

        try (Connection conn = Station.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int rowsDeleted = pstmt.executeUpdate();  // Executes the deletion
            System.out.println("Deleted " + rowsDeleted + " rows from station_values table.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

