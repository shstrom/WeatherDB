package com.testprosjekt.WeatherDB.frost;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class Frost {

    public static void main(String[] args) {


        ArrayList<String> stations = new ArrayList<>();
        try {
            // Insert your own client ID
            String client_id = "cfb662ba-dfac-456a-b854-88fefbf51a9e";
            // Build the URL and define parameters
            String url = "https://frost.met.no/sources/v0.jsonld?elements=air_temperature&municipality=Oslo";
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
            JSONArray id = object.getJSONArray("data");
            // Loop through the data
            for (int i = 0; i < id.length(); i++) {
                object = id.getJSONObject(i);
                if (!stations.contains(object.getString("id")))stations.add (object.getString("id"));
            }
        } catch (Exception ex) {
            System.out.println("Error: the data retrieval was not successful!");
            ex.printStackTrace();
        }
        String osloStations = (stations.toString());
        osloStations= osloStations.replaceAll("[ \\]]", "").replaceAll("\\[", "");


        try {
            // Insert your own client ID
            String client_id = "cfb662ba-dfac-456a-b854-88fefbf51a9e";
            // Build the URL and define parameters
            String url = "https://frost.met.no/observations/v0.jsonld?";
            url += "sources=" + osloStations;
            url += "&elements=" + "mean(air_temperature P1D)";//bedre kode senere?
            url += "&referencetime=" + "2024-09-01/2024-10-01";
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
                System.out.println("\n" + object.getString("sourceId") + "  " + object.getString("referenceTime"));
                observations = object.getJSONArray("observations");
                // new Station(object.getString("sourceId"), object.getString("name"), object.getString("shortName"), object.getString("coordinates"));
                for (int j = 0; j < observations.length(); j++) {
                    object = observations.getJSONObject(j);
                    System.out.println(" " + object.getString("elementId") + "=" + object.getInt("value") + "  (" + object.getString("timeOffset") + ")");
                }
            }
        } catch (Exception ex) {
            System.out.println("Error: the data retrieval was not successful!");
            ex.printStackTrace();
        }
        System.out.println(stations);
        System.out.println(osloStations);
    }
}
