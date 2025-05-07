package com.example.airPlan.controllers.Agence;

import com.example.airPlan.Services.FlightServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class StatsController implements Initializable {

    @FXML private BarChart<String, Number> barChartFlights;
    @FXML private ComboBox<String> statTypeComboBox;
    @FXML private Label chartTitle;
    @FXML private Label lastUpdatedLabel;
    @FXML private Label statusLabel;

    private final FlightServices flightService = new FlightServices();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupChart();
        setupStatTypeSelector();
        updateStatus("Ready to display statistics");
        loadInitialData();
    }

    private void setupChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Category");
        xAxis.setTickLabelRotation(-45);  // Rotate labels for better readability
        xAxis.setTickLabelFont(Font.font("Arial", 12));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of Flights");
        yAxis.setTickUnit(1);  // Show every integer value
        yAxis.setMinorTickVisible(false);
        yAxis.setTickLabelFont(Font.font("Arial", 12));

        barChartFlights.setTitle("");
        barChartFlights.setLegendVisible(false);
        barChartFlights.setAnimated(true);
        barChartFlights.setCategoryGap(10);
        barChartFlights.setBarGap(2);
    }

    private void setupStatTypeSelector() {
        ObservableList<String> statTypes = FXCollections.observableArrayList(
                "Flights by Airline",
                "Flights by Status",
                "Popular Routes",
                "Monthly Flights"
        );
        statTypeComboBox.setItems(statTypes);
        statTypeComboBox.getSelectionModel().selectFirst();

        statTypeComboBox.setOnAction(event -> {
            String selected = statTypeComboBox.getValue();
            chartTitle.setText(selected);
            refreshChartData();
        });
    }

    private void loadInitialData() {
        chartTitle.setText(statTypeComboBox.getValue());
        refreshChartData();
    }

    private void refreshChartData() {
        updateStatus("Loading data...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String selectedStat = statTypeComboBox.getValue();
                XYChart.Series<String, Number> series = new XYChart.Series<>();

                Platform.runLater(() -> {
                    try {
                        switch (selectedStat) {
                            case "Flights by Airline":
                                loadAirlineStats(series);
                                break;
                            case "Flights by Status":
                                loadStatusStats(series);
                                break;
                            case "Popular Routes":
                                loadRouteStats(series);
                                break;
                            case "Monthly Flights":
                                loadMonthlyStats(series);
                                break;
                        }
                        updateChart(series);
                        updateStatus("Data loaded successfully");
                    } catch (Exception e) {
                        updateStatus("Error loading data: " + e.getMessage());
                    }
                });
                return null;
            }
        };

        new Thread(task).start();
    }

    private void loadAirlineStats(XYChart.Series<String, Number> series) {
        Map<String, Integer> stats = flightService.getFlightCountByAirline();
        series.setName("Airlines");

        stats.forEach((airline, count) -> {
            series.getData().add(new XYChart.Data<>(airline, count));
        });
    }

    private void loadStatusStats(XYChart.Series<String, Number> series) {
        Map<String, Integer> stats = flightService.getFlightCountByStatus();
        series.setName("Status");

        stats.forEach((status, count) -> {
            series.getData().add(new XYChart.Data<>(status, count));
        });
    }

    private void loadRouteStats(XYChart.Series<String, Number> series) {
        Map<String, Integer> stats = flightService.getPopularRoutes(5);
        series.setName("Routes");

        stats.forEach((route, count) -> {
            series.getData().add(new XYChart.Data<>(route, count));
        });
    }

    private void loadMonthlyStats(XYChart.Series<String, Number> series) {
        Map<String, Integer> stats = flightService.getFlightsByMonth();
        series.setName("Months");
        Map<String, Integer> sortedStats = new TreeMap<>(stats);
        series.getData().clear();
        sortedStats.forEach((month, count) -> {
            series.getData().add(new XYChart.Data<>(month, count));
        });
    }

    private void updateChart(XYChart.Series<String, Number> series) {
        barChartFlights.getData().clear();
        barChartFlights.getData().add(series);

        // Apply precise styling
        String style = switch (statTypeComboBox.getValue()) {
            case "Flights by Airline" -> "-fx-bar-fill: #4285F4;";
            case "Flights by Status" -> "-fx-bar-fill: #EA4335;";
            case "Popular Routes" -> "-fx-bar-fill: #34A853;";
            case "Monthly Flights" -> "-fx-bar-fill: #FBBC05;";
            default -> "-fx-bar-fill: #4285F4;";
        };

        // Precise bar styling
        for (XYChart.Data<String, Number> data : series.getData()) {
            Node node = data.getNode();
            if (node != null) {
                node.setStyle(style +
                        " -fx-background-radius: 3 3 0 0;" +
                        " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 2, 0.5, 0, 1);");

                // Add precise value labels
                Label label = new Label(data.getYValue().toString());
                label.setStyle("-fx-font-size: 10px; -fx-text-fill: #555;");
                StackPane.setAlignment(label, Pos.TOP_CENTER);
                StackPane.setMargin(label, new Insets(0, 0, 5, 0));
                ((StackPane) node).getChildren().add(label);
            }
        }

        // Handle monthly data formatting
        if (statTypeComboBox.getValue().equals("Monthly Flights")) {
            // Pre-process the data to format months before adding to series
            XYChart.Series<String, Number> formattedSeries = new XYChart.Series<>();
            formattedSeries.setName("Months");

            series.getData().forEach(data -> {
                String month = data.getXValue();
                String formattedMonth = formatMonth(month); // Format as MM/YY
                formattedSeries.getData().add(new XYChart.Data<>(formattedMonth, data.getYValue()));
            });

            barChartFlights.getData().clear();
            barChartFlights.getData().add(formattedSeries);
        }
        if (statTypeComboBox.getValue().equals("Monthly Flights")) {
            barChartFlights.getStyleClass().add("monthly-chart");
        } else {
            barChartFlights.getStyleClass().remove("monthly-chart");
        }
    }

    private String formatMonth(String yyyyMM) {
        try {
            // Input format: "YYYY-MM"
            String year = yyyyMM.substring(2, 4); // Last 2 digits of year
            String month = yyyyMM.substring(5);    // Month number
            return month + "/" + year;             // Output format: "MM/YY"
        } catch (Exception e) {
            return yyyyMM; // Fallback to original format if parsing fails
        }
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
        lastUpdatedLabel.setText(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    private void showAlert(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}