package com.example.airPlan.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.airPlan.models.reclamation;
import com.example.airPlan.Services.reclamationService;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class AffRecAd {
    @FXML
    private ListView<reclamation> listViewRec;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private BarChart<String, Number> statusBarChart;
    @FXML
    private PieChart typePieChart;

    private final reclamationService reclamationService = new reclamationService();
    private final ObservableList<reclamation> originalList = FXCollections.observableArrayList();
    private final FilteredList<reclamation> filteredList = new FilteredList<>(originalList);
    private final SortedList<reclamation> sortedList = new SortedList<>(filteredList);

    @FXML
    public void initialize() {
        try {
            loadReclamations();
            configureCellFactory();
            setupSearchFunctionality();
            setupFilterFunctionality();
            loadStatusBarChart();
            loadTypePieChart();
        } catch (Exception e) {
            showAlert("Initialization Error", "Failed to initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadReclamations() {
        List<reclamation> reclamations = reclamationService.display();
        originalList.clear();
        originalList.addAll(reclamations);
        listViewRec.setItems(sortedList);
    }

    private void setupSearchFunctionality() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(rec -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return rec.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                        rec.getType().toLowerCase().contains(lowerCaseFilter) ||
                        rec.getStatut().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void setupFilterFunctionality() {
        filterComboBox.getItems().addAll("ID", "Type", "Date", "Description", "Rating", "Status");
        filterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                sortReclamationsByAttribute(newValue);
            }
        });
        sortedList.setComparator(Comparator.comparing(reclamation::getId)); // Default sort by ID
    }

    private void sortReclamationsByAttribute(String attribute) {
        Comparator<reclamation> comparator = switch (attribute) {
            case "ID" -> Comparator.comparing(reclamation::getId);
            case "Type" -> Comparator.comparing(reclamation::getType);
            case "Date" -> Comparator.comparing(reclamation::getDatee);
            case "Description" -> Comparator.comparing(reclamation::getDescription);
            case "Rating" -> Comparator.comparingInt(reclamation::getNote);
            case "Status" -> Comparator.comparing(reclamation::getStatut);
            default -> null;
        };
        sortedList.setComparator(comparator);
    }

    private void loadStatusBarChart() {
        try {
            // Clear any existing data
            statusBarChart.getData().clear();

            // Set chart title and axes labels - using the chart directly, not the axes
            statusBarChart.setTitle("Reclamation Status");

            // Get data from service
            Map<String, Integer> statusCounts = reclamationService.fetchReclamationCountsByStatus();

            // Create series for the chart
            XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
            dataSeries.setName("Reclamation Status");

            // Add data points to the series
            statusCounts.forEach((status, count) ->
                    dataSeries.getData().add(new XYChart.Data<>(status, count))
            );

            // Add series to the chart
            statusBarChart.getData().add(dataSeries);

            // Optional: Style the chart
            statusBarChart.setLegendVisible(false);  // Hide legend if not needed

        } catch (Exception e) {
            showAlert("Chart Error", "Failed to load status chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTypePieChart() {
        try {
            // Clear any existing data
            typePieChart.getData().clear();

            // Set chart title
            typePieChart.setTitle("Reclamation Types Distribution");

            // Get data from service
            Map<String, Integer> typeCounts = reclamationService.fetchReclamationCountsByType();

            // Create data for the pie chart
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            // Add data points to the pie chart
            typeCounts.forEach((type, count) ->
                    pieChartData.add(new PieChart.Data(type, count))
            );

            // Set the data to the chart
            typePieChart.setData(pieChartData);

        } catch (Exception e) {
            showAlert("Chart Error", "Failed to load type chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configureCellFactory() {
        listViewRec.setCellFactory(param -> new ListCell<reclamation>() {
            @Override
            protected void updateItem(reclamation rec, boolean empty) {
                super.updateItem(rec, empty);
                if (empty || rec == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox container = new VBox(5);
                    HBox firstLine = new HBox(10);
                    HBox secondLine = new HBox(10);
                    HBox thirdLine = new HBox(10);

                    Label idLabel = new Label("ID: " + rec.getId());
                    Label typeLabel = new Label("Type: " + rec.getType());
                    Label dateLabel = new Label("Date: " + rec.getDatee());
                    Label descLabel = new Label("Description: " + rec.getDescription());
                    Label noteLabel = new Label("Rating: " + rec.getNote());
                    Label statusLabel = new Label("Status: " + rec.getStatut());

                    // Apply styles
                    descLabel.setStyle("-fx-font-weight: bold;");
                    String statusColor = rec.getStatut().equalsIgnoreCase("résolu") ? "green" : "red";
                    statusLabel.setStyle("-fx-text-fill: " + statusColor + ";");

                    // Layout organization
                    firstLine.getChildren().addAll(idLabel, typeLabel, dateLabel);
                    secondLine.getChildren().add(descLabel);
                    thirdLine.getChildren().addAll(noteLabel, statusLabel);

                    container.getChildren().addAll(firstLine, secondLine, thirdLine);
                    setGraphic(container);
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void feed(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Admin/affichage_feed.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("list reclamation");
            stage.show();

         //   stage.setOnHidden(event -> actionEvent());
        } catch (IOException e) {
            showAlert("Error", "Failed to load add form: " + e.getMessage());
            e.printStackTrace();
        }
    }
}