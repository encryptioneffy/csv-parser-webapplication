package edu.brown.cs32.examples.moshiExample.server.weather;

/**
 * All cordinates are of this type, gets euclidean distance
 */
public record Coordinates (double lat, double longi) {

    public double getDistance(Coordinates coords) {
        return Math.sqrt(Math.pow((coords.lat() - this.lat),2) + Math.pow((coords.longi() - this.longi),2));
    }
}
