package com.weather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeocodeApiClient {
    private String apiKey;
    private String country;

    public GeocodeApiClient(String apiKey, String country) {
        this.apiKey = apiKey;
        this.country = country;
    }

    public GeoCoordinates getCountryCoordinates() throws WeatherAppException {
        // Build the API request URL
        String apiUrl = "http://api.openweathermap.org/geo/1.0/direct?q=" + country + "&limit=1&appid=" + apiKey;

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
                JSONArray jsonArray = new JSONArray(responseBody);

                // Get GeoCoordinate information
                if (!jsonArray.isEmpty()) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    BigDecimal lat = jsonObject.getBigDecimal("lat").setScale(2, RoundingMode.HALF_UP);
                    BigDecimal lon = jsonObject.getBigDecimal("lon").setScale(2, RoundingMode.HALF_UP);

                    return new GeoCoordinates(lon, lat);
                } else {
                    throw new WeatherAppException("Request failed: The response is empty");
                }

            } else {
                // Request failed
                throw new WeatherAppException("Request failed with status code: " + statusCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new WeatherAppException("Error sending the HTTP request: " + e.getMessage(), e);
        }
    }
}
