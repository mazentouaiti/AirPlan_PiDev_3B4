package com.example.airPlan.controllers.Admin;

import com.example.airPlan.models.Hebergement;
import com.example.airPlan.Services.ServiceHebergement;
import com.example.airPlan.views.AccCellAdminFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AccAdmin implements Initializable {
    @FXML
    private TextField namefilteragence;
    @FXML
    private ComboBox<String> dispocomboagence;
    @FXML
    private TextField countryfilteragence;
    @FXML
    private ComboBox<String> typecombifilteragence;
    @FXML
    private ListView<Hebergement> listHebergement;
    @FXML
    private BorderPane adminParent;

    private ObservableList<Hebergement> list;
    private FilteredList<Hebergement> filteredList;
    private ServiceHebergement service;
    private Parent accView; // Store reference to our own view


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accView = listHebergement.getParent();
        try {
            service = new ServiceHebergement();
            loadHebergements();
            setupSearchFilters();
        } catch (Exception e) {
            showErrorAlert("Initialization Error", "Failed to initialize controller: " + e.getMessage());
        }

        listHebergement.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Hebergement selected = listHebergement.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    ouvrirFenetreDetails(selected);
                }
            }
        });
    }

    private void loadHebergements() {
        try {
            List<Hebergement> hebergements = service.afficher();
            list = FXCollections.observableArrayList(hebergements);
            filteredList = new FilteredList<>(list, p -> true);

            listHebergement.setItems(filteredList);
            listHebergement.setCellFactory(param -> new AccCellAdminFactory());
        } catch (Exception e) {
            showErrorAlert("Loading Error", "Failed to load hebergements: " + e.getMessage());
        }
    }

    private void setupSearchFilters() {
        namefilteragence.textProperty().addListener((obs, oldValue, newValue) -> applySearch());
        countryfilteragence.textProperty().addListener((obs, oldValue, newValue) -> applySearch());
        dispocomboagence.valueProperty().addListener((obs, oldValue, newValue) -> applySearch());
        typecombifilteragence.valueProperty().addListener((obs, oldValue, newValue) -> applySearch());
        typecombifilteragence.getItems().addAll("Hotel", "House", "Apartment", "Villa","Hostel","Bungalow");
        dispocomboagence.getItems().addAll("waiting", "accepted", "rejected");
    }

    private void applySearch() {
        String nameFilter = namefilteragence.getText().toLowerCase();
        String countryFilter = countryfilteragence.getText().toLowerCase();
        String dispoFilter = dispocomboagence.getValue() != null ? dispocomboagence.getValue().toLowerCase() : "";
        String typeFilter = typecombifilteragence.getValue() != null ? typecombifilteragence.getValue().toLowerCase() : "";

        filteredList.setPredicate(hebergement -> {
            boolean matchName = hebergement.getName().toLowerCase().contains(nameFilter);
            boolean matchCountry = hebergement.getCountry().toLowerCase().contains(countryFilter);
            boolean matchDispo = dispoFilter.isEmpty() || hebergement.getStatus().toLowerCase().contains(dispoFilter);
            boolean matchType = typeFilter.isEmpty() || hebergement.getType().toLowerCase().contains(typeFilter);

            return matchName && matchCountry && matchDispo && matchType;
        });
    }

    public void ouvrirFenetreDetails(Hebergement hebergement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/hotel_info_admin.fxml"));
            Parent detailsView = loader.load();

            HotelInfoAdmin controller = loader.getController();
            controller.setHebergementDetails(hebergement);

            // Pass the accView reference to the details controller
            controller.setReturnView(accView);

            // Get the scene's root (should be BorderPane)
            BorderPane root = (BorderPane) listHebergement.getScene().getRoot();
            root.setCenter(detailsView);

        } catch (IOException e) {
            showErrorAlert("Error", "Failed to load hotel details: " + e.getMessage());
        }
    }


    @FXML
    public void onReset(ActionEvent actionEvent) {
        namefilteragence.clear();
        countryfilteragence.clear();
        dispocomboagence.setValue(null);
        typecombifilteragence.setValue(null);
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private Button btnStat;


    @FXML
    private void showStatistics(ActionEvent event) {
        try {
            // Get statistics data
            Map<String, Long> stats = service.getStatusStatistics();
            long total = stats.values().stream().mapToLong(Long::longValue).sum();

            // Create pie chart data with percentage labels
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            // Add data points with percentage calculation
            stats.forEach((status, count) -> {
                double percentage = total > 0 ? (count * 100.0 / total) : 0;
                PieChart.Data data = new PieChart.Data(
                        String.format("%s (%.1f%%)", status, percentage),
                        count
                );
                pieChartData.add(data);
            });

            // Create pie chart
            PieChart pieChart = new PieChart(pieChartData);
            pieChart.setTitle("Accommodation Status Statistics");
            pieChart.setLabelsVisible(true);
            pieChart.setLegendVisible(true);
            pieChart.setPrefSize(400, 400);

            // Style the chart slices with colors
            pieChart.setStyle("-fx-font-size: 14px;");
            pieChart.getData().forEach(data -> {
                String color = switch (data.getName().split(" ")[0]) { // Get status before percentage
                    case "accepted" -> "#4CAF50"; // Green
                    case "waiting" -> "#FFC107"; // Amber
                    case "rejected" -> "#F44336"; // Red
                    default -> "#9E9E9E"; // Grey
                };
                data.getNode().setStyle("-fx-pie-color: " + color + ";");
            });

            // Create popup window
            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Accommodation Statistics");

            // Add total count label
            Label totalLabel = new Label(String.format("Total Accommodations: %d", total));
            totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #588b8b;");

            VBox layout = new VBox(10);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(20));
            layout.getChildren().addAll(totalLabel, pieChart);

            // Add close button
            Button closeButton = new Button("Close");
            closeButton.setStyle("-fx-background-color: #588b8b; -fx-text-fill: white;");
            closeButton.setOnAction(e -> popup.close());
            layout.getChildren().add(closeButton);

            Scene scene = new Scene(layout);
            popup.setScene(scene);
            popup.showAndWait();

        } catch (SQLException e) {
            showErrorAlert("Statistics Error", "Failed to load statistics: " + e.getMessage());
        }
    }
}
