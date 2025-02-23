package com.weather;

import java.math.BigDecimal;

public class GeoCoordinates {
    private BigDecimal lon;
    private BigDecimal lat;

    public GeoCoordinates(BigDecimal lon, BigDecimal lat) {
        this.lat = lat;
        this.lon = lon;
    }

    // Getter methods for the GeoCoordinates
    public BigDecimal getLat() {
        return lat;
    }

    public BigDecimal getLon() {
        return lon;
    }
}