package com.testprosjekt.WeatherDB.frost;

import java.util.ArrayList;

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
        this.values = null;
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

    public ArrayList<String[]> getValues() {
        return values;
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
        return "Station{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", coordinates='" + coordinates + '\'' +
                ", distance=" + distance +
                ", values=" + values +
                '}';
    }
}
