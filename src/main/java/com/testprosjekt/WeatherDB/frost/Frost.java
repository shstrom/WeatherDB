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

        //hente client id fra env
        Dotenv dotenv = Dotenv.load();
        final String CLIENTID = dotenv.get("CLIENT_ID");

        ArrayList<Station> stations = new ArrayList<>();
        String stationId = "";

        //hente ut ID på stasjonene som ligger i Oslo
        try {
            String url = "https://frost.met.no/sources/v0.jsonld?elements=air_temperature&county=Oslo";
            // Replace spaces
            url = url.replaceAll(" ", "%20");
            // Issue an HTTP GET request
            URL Url = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) Url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64((CLIENTID + ":").getBytes("UTF-8"))));
            // Extract JSON data
            JSONObject object = new JSONObject(new JSONTokener(new InputStreamReader(conn.getInputStream())));
            JSONArray data = object.getJSONArray("data");
            //JSONArray geometry;
            // Loop through the data
            for (int i = 0; i < data.length(); i++) {
                object = data.getJSONObject(i);
                Station station = new Station(object.getString("id"), object.getString("name"), object.getString("shortName"));

                JSONObject g = object.getJSONObject("geometry");
                String coordinates = String.format("%s", g.get("coordinates"));
                station.setCoordinates(coordinates);
                station.calculateDistance();

                if (!stations.contains(station))stations.add (station);
                stationId = stationId + object.getString("id") + ",";


            }
        } catch (Exception ex) {
            System.out.println("Error: the data retrieval was not successful!");
            ex.printStackTrace();
        }

        //sette tidsperiode
        LocalDate today = LocalDate.now().minusDays(1);
        LocalDate sevenDaysAgo = today.minusDays(7);
        String dateRange = sevenDaysAgo + "/" + today;

        //hente ut verdier fra aktuelle stasjoner
        try {

            StringBuilder url = new StringBuilder();

            url.append("https://frost.met.no/observations/v0.jsonld?");
            url.append("sources=");
            url.append(stationId);
            url.append("&elements=");
            url.append("mean(air_temperature%20P1D)");
            url.append("&referencetime=");
            url.append(dateRange);
            url.append("&levels=default");
            url.append("&timeoffsets=default");



            //String url = "https://frost.met.no/observations/v0.jsonld?";
            //url += "sources=" + stationId;
            //url += "&elements=" + "mean(air_temperature P1D)";//bedre kode senere?
            //url += "&referencetime=" + dateRange;
            //url += "&levels=default";
            //url += "&timeoffsets=default";
            //url = url.replaceAll(" ", "%20");

            URL Url = new URL(url.toString());
            HttpsURLConnection conn = (HttpsURLConnection) Url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64((CLIENTID + ":").getBytes("UTF-8"))));
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

        findClosestStation(stations);
        clearTable();
        Station.createNewTable();
        updateDatabase(stations);

    }

    public static void findClosestStation(ArrayList <Station> stations){
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
    }
    public static void updateDatabase (ArrayList<Station> stations){
        //sjekke om stasjonen er nærmest, og skrive til database
        for (Station s : stations){
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

