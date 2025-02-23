package com.weather;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WeatherApp extends JFrame implements ActionListener {
    private final String apiKey = "f0146643ce9cd5158736f3868aeb15c3";
    private JButton button;
    private JTextField countryInput;
    private JPanel forecastPanel;

    public WeatherApp() {
        initializeUi();
    }

    private void initializeUi() {
        // Set up all the components for the ui

        JLabel countryLabel = new JLabel();
        countryLabel.setBounds(420, 10, 160, 50);
        countryLabel.setText("Your Country or State or City");

        countryInput = new JTextField();
        countryInput.setBounds(450, 60, 100, 20);
        countryInput.setToolTipText("Enter Country State or City");
        countryInput.setText("London");

        button = new JButton();
        button.setBounds(425, 90, 150, 20);
        button.setText("Get Forecast");
        button.addActionListener(this);


        forecastPanel = new JPanel();
        forecastPanel.setBounds(10, 120, 960, 400);
        FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER, 20, 20);
        forecastPanel.setLayout(flowLayout);
        forecastPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(forecastPanel);
        scrollPane.setBounds(10, 120, 960, 400);

        add(scrollPane);
        add(button);
        add(countryInput);
        add(countryLabel);
        setupFrame();
    }

    private JPanel createWeatherPanel(WeatherData data) {
        JPanel weatherPanel = new JPanel();
        weatherPanel.setPreferredSize(new Dimension(300, 300));
        weatherPanel.setLayout(new BoxLayout(weatherPanel, BoxLayout.Y_AXIS));
        weatherPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        weatherPanel.setBackground(new Color(0xDCDCDC));

        // Create a panel for the image with center alignment
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imagePanel.setBackground(new Color(0xDCDCDC));
        ImageIcon imageIcon = loadImageIcon(data.getImgUrl());
        JLabel imageLabel = new JLabel(imageIcon);
        imagePanel.add(imageLabel);

        JLabel dateLabel = new JLabel("Date: " + data.getDate());
        JLabel tempLabel = new JLabel("Temperature: " + data.getTemp() + "℉");
        JLabel humidityLabel = new JLabel("Humidity: " + data.getHumidity() + "%");
        JLabel windSpeedLabel = new JLabel("WindSpeed: " + data.getWindSpeed() + "m/s");
        JLabel descriptionLabel = new JLabel("Description: " + data.getDescription());

        // Set center alignment for the labels
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        humidityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        windSpeedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add vertical glue to center the components vertically
        weatherPanel.add(Box.createVerticalGlue());

        // Add the image panel and labels to the weather panel
        weatherPanel.add(imagePanel);
        weatherPanel.add(dateLabel);
        weatherPanel.add(descriptionLabel);
        weatherPanel.add(tempLabel);
        weatherPanel.add(humidityLabel);
        weatherPanel.add(windSpeedLabel);

        // Add vertical glue to center the components vertically
        weatherPanel.add(Box.createVerticalGlue());

        return weatherPanel;
    }

    private ImageIcon loadImageIcon(String imageUrlString) {
        ImageIcon imageIcon = null;
        try {
            // Get the image from the URL
            URI imageUrlUri = new URI(imageUrlString);
            URL imageUrl = imageUrlUri.toURL();
            imageIcon = new ImageIcon(imageUrl);
        } catch (IOException | URISyntaxException e) {
            displayErrorMessage("Image could not be loaded");
            try {
                // Get null image
                URI nullImageUri = new URI("file:///path/to/null_image.png");
                URL nullImageUrl = nullImageUri.toURL();
                imageIcon = new ImageIcon(nullImageUrl);
            } catch (URISyntaxException | MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
        return imageIcon;
    }

    private void setupFrame() {
        // Set up the frame
        setTitle("Weather App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        setSize(1000, 570);
        setLocationRelativeTo(null);
        ImageIcon image = new ImageIcon("src/main/java/com/weather/sun_icon.png");
        setIconImage(image.getImage());
        getContentPane().setBackground(new Color(245, 234, 250));
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            processWeatherData();
        }
    }

    private void processWeatherData() {
        String country = countryInput.getText();
        if (!country.isEmpty()) {
            try {
                GeoCoordinates coordinates = getCountryCoordinates(country);
                ArrayList<WeatherData> weatherDataList = getWeatherDataList(coordinates);
                ArrayList<WeatherData> filteredForecast = filterWeatherData(weatherDataList);
                displayFilteredForecast(filteredForecast);
            } catch (WeatherAppException exception) {
                displayErrorMessage(exception.getMessage());
            }
        } else {
            displayErrorMessage("Please enter your country, state, or city");
        }
    }

    private GeoCoordinates getCountryCoordinates(String country) throws WeatherAppException {
        // Get coordinated from the country name
        GeocodeApiClient geocodeApiClient = new GeocodeApiClient(apiKey, country);
        return geocodeApiClient.getCountryCoordinates();
    }

    private ArrayList<WeatherData> getWeatherDataList(GeoCoordinates coordinates) throws WeatherAppException {
        // Get the weather list from the Api with the new coordinates
        WeatherApiClient weatherApiClient = new WeatherApiClient(apiKey, coordinates.getLat(), coordinates.getLon());
        return weatherApiClient.getWeatherDataList();
    }

    private ArrayList<WeatherData> filterWeatherData(ArrayList<WeatherData> weatherDataList) {
        // Filter the list to only get the 6 day forecast
        ArrayList<WeatherData> filteredForecast = new ArrayList<>();
        Set<String> uniqueDates = new HashSet<>();

        for (WeatherData weatherData : weatherDataList) {
            String date = weatherData.getDate();
            if (!uniqueDates.contains(date)) {
                uniqueDates.add(date);
                filteredForecast.add(weatherData);
            }
        }

        return filteredForecast;
    }

    private void displayFilteredForecast(ArrayList<WeatherData> filteredForecast) {
        // Display to JFrame
        forecastPanel.removeAll();
        for (WeatherData data : filteredForecast) {
            forecastPanel.add(createWeatherPanel(data));
        }
        revalidate();
        repaint();
    }

    private void displayErrorMessage(String errorMessage) {
        // Display custom error message
        JOptionPane.showMessageDialog(null, errorMessage, "There was an error!", JOptionPane.ERROR_MESSAGE);
    }
}
