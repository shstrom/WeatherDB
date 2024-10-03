package com.testprosjekt.WeatherDB.frost;

public class Station {
    String id;
    String name;
    String shortName;
    String coordinates;
    Double distance;

    public Station(String id, String name, String shortName, String coordinates) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.coordinates = coordinates;
        this.distance = 1000.00;
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

    public void calculateDistance(){
        String [] s = this.coordinates.split(",");
        double doubleX = Double.parseDouble(s[0]);
        double doubleY = Double.parseDouble(s[1]);
        double xDiff = Math.abs(doubleX - 10.7725);
        double yDiff = Math.abs(doubleY - 59.9022);
        this.setDistance(xDiff + yDiff);
    }


}
