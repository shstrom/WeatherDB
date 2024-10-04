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


        ArrayList<Station> stations = new ArrayList<>();
        String stationId = "";
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
            JSONArray data = object.getJSONArray("data");
            //JSONArray geometry;
            // Loop through the data
            for (int i = 0; i < data.length(); i++) {
                object = data.getJSONObject(i);

                Station station = new Station(object.getString("id"), object.getString("name"), object.getString("shortName"));


                //}

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

        String osloStations= stationId.replaceAll("[ \\]]", "").replaceAll("\\[", "");


        try {
            // Insert your own client ID
            String client_id = "cfb662ba-dfac-456a-b854-88fefbf51a9e";
            // Build the URL and define parameters
            String url = "https://frost.met.no/observations/v0.jsonld?";
            url += "sources=" + osloStations;
            url += "&elements=" + "mean(air_temperature P1D)";//bedre kode senere?
            url += "&referencetime=" + "2024-10-01/2024-10-03";
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
                // FÅR IKKE TIL Å LEGGE TIL VERDIENE TIL ARRAYLISTEN
                String x = object.getString("referenceTime");
                String compId = object.getString("sourceId").substring(0, 7);
                observations = object.getJSONArray("observations");
                for (Station s : stations){
                    if (compId.equals(s.getId())){
                        String y = "";
                        for (int j = 0; j < observations.length(); j++) {
                            JSONObject objectO = observations.getJSONObject(j);
                            var v = objectO.getBigDecimal("value");
                            //var v = o;
                            //y = object.getString("value");
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
        //System.out.println(stations);
        //System.out.println(osloStations);
        for (Station s : stations){
            System.out.println(s.toString());
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
    }
}
