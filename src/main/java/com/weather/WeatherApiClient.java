package com.weather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class WeatherApiClient {
    private String apiKey;
    private BigDecimal lat;
    private BigDecimal lon;

    public WeatherApiClient(String apiKey, BigDecimal lat, BigDecimal lon) {
        this.apiKey = apiKey;
        this.lat = lat;
        this.lon = lon;
    }

    public ArrayList<WeatherData> getWeatherDataList() throws WeatherAppException {
        // Build the API request URL
        String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&units=Imperial&appid=" + apiKey;

        // Create an instance of HttpRequest with the API URL
        HttpRequest httpRequest;
        try {
            httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl))
                    .build();
        } catch (URISyntaxException e) {
            throw new WeatherAppException("Invalid API URL: " + apiUrl, e);
        }

        // Send the HTTP GET request
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // Check the response status code
            int statusCode = response.statusCode();
            if (statusCode == 200) {
                // Request was successful
                String responseBody = response.body();

                // Parse the JSON response
                JSONObject json = new JSONObject(responseBody);
                // Access the "list" array containing forecasts
                JSONArray forecastList = json.getJSONArray("list");
                // create an ArrayList of WeatherData objects
                ArrayList<WeatherData> forecasts = new ArrayList<WeatherData>();

                // Iterate over each forecast
                for (int i = 0; i < forecastList.length(); i++) {
                    JSONObject forecast = forecastList.getJSONObject(i);

                    // Extract weather information
                    String description = forecast.getJSONArray("weather").getJSONObject(0).getString("description");
                    String date = forecast.getString("dt_txt").split(" ")[0];
                    int temp = forecast.getJSONObject("main").getInt("temp");
                    int humidity = forecast.getJSONObject("main").getInt("humidity");
                    double windSpeed = forecast.getJSONObject("wind").getDouble("speed");
                    String iconCode = forecast.getJSONArray("weather").getJSONObject(0).getString("icon");
                    String imgUrl = "http://openweathermap.org/img/w/" + iconCode + ".png";

                    // Create and add new WeatherData object to the WeatherData list
                    WeatherData weatherData = new WeatherData(description, date, temp, humidity, windSpeed, imgUrl);
                    forecasts.add(weatherData);
                }
                return forecasts;
            } else {
                // Request failed
                throw new WeatherAppException("Request failed with status code: " + statusCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new WeatherAppException("Error sending the HTTP request: " + e.getMessage(), e);
        }
    }
}