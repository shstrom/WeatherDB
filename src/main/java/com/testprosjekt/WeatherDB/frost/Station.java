package com.testprosjekt.WeatherDB.frost;

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

    public void writeToDb () {



    }
}
