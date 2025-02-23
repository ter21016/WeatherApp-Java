package com.weather;

public class WeatherData {
    private String description;
    private String date;
    private int temp;
    private int humidity;
    private double windSpeed;
    private String imgUrl;

    public WeatherData(String description, String date, int temp, int humidity, double windSpeed, String imgUrl) {
        this.description = description;
        this.date = date;
        this.temp = temp;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.imgUrl = imgUrl;
    }

    // Getter methods for the weather data
    public int getTemp() {
        return temp;
    }

    public int getHumidity() {
        return humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}