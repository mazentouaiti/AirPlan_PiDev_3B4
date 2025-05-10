package com.example.airPlan.controllers.Agence;

import com.example.airPlan.models.Hebergement;
import com.example.airPlan.Services.ServiceHebergement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AccController implements Initializable {

    @FXML
    private ComboBox<String> dispocomboagence;
    @FXML
    private TextField countryfilteragence;
    @FXML
    private ListView<Hebergement> listHebergement;
    private ObservableList<Hebergement> list;
    private ServiceHebergement service;
    private BorderPane agency_parent;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        service = new ServiceHebergement();

        // Initialize the combo box with filter options
        dispocomboagence.getItems().addAll(
                "All",
                "Status: Waiting",
                "Status: Accepted",
                "Status: Rejected",
                "Type: Hotel",
                "Type: House",
                "Type: Apartment",
                "Type: Villa",
                "Type: Hostel",
                "Type: Bungalow"
        );
        dispocomboagence.getSelectionModel().selectFirst();

        // Set up listeners for filters
        countryfilteragence.textProperty().addListener((observable, oldValue, newValue) -> {
            filterHebergements();
        });

        dispocomboagence.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterHebergements();
        });

        // Rest of your existing initialize code...
        // Update the cell factory to pass agencyParent
        listHebergement.setCellFactory(lv -> new ListCell<Hebergement>() {
            @Override
            protected void updateItem(Hebergement hebergement, boolean empty) {
                super.updateItem(hebergement, empty);
                if (empty || hebergement == null) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Agences/AccCell_agency.fxml"));
                        AnchorPane pane = loader.load();
                        AccCellController controller = loader.getController();

                        // Add this line to pass the parent reference
                        controller.setAgencyParent(agency_parent);

                        controller.setListHebergement(listHebergement);
                        controller.setHebergement(hebergement);
                        setGraphic(pane);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        loadHebergements();

        listHebergement.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Hebergement selected = listHebergement.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    ouvrirFenetreDetails(selected);
                }
            }
        });
    }
    public void setAgencyParent(BorderPane agency_parent) {
        this.agency_parent = agency_parent;
    }
    private void filterHebergements() {
        String searchText = countryfilteragence.getText().toLowerCase();
        String selectedFilter = dispocomboagence.getValue();

        if (list == null) return;

        ObservableList<Hebergement> filteredList = list.filtered(hebergement -> {
            // Search filter (name, city, or country)
            boolean matchesSearch = searchText.isEmpty() ||
                    hebergement.getName().toLowerCase().contains(searchText) ||
                    hebergement.getCity().toLowerCase().contains(searchText) ||
                    hebergement.getCountry().toLowerCase().contains(searchText);

            // Combo box filter
            boolean matchesCombo = true;
            if (selectedFilter != null && !selectedFilter.equals("All")) {
                if (selectedFilter.startsWith("Status:")) {
                    String status = selectedFilter.substring(8); // Remove "Status: "
                    matchesCombo = hebergement.getStatus().equalsIgnoreCase(status);
                } else if (selectedFilter.startsWith("Type:")) {
                    String type = selectedFilter.substring(6); // Remove "Type: "
                    matchesCombo = hebergement.getType().equalsIgnoreCase(type);
                }
            }

            return matchesSearch && matchesCombo;
        });

        listHebergement.setItems(filteredList);
    }

    // Rest of your existing methods...
    private void loadHebergements() {
        try {
            List<Hebergement> hebergements = service.afficher();
            list = FXCollections.observableArrayList(hebergements);
            filterHebergements(); // Apply any existing filters when loading
        } catch (Exception e) {
            showErrorAlert("Loading Error", "Failed to load hébergements: " + e.getMessage());
        }
    }

    @FXML
    public void onReset(ActionEvent actionEvent) {
        countryfilteragence.clear();
        dispocomboagence.getSelectionModel().selectFirst();
        loadHebergements();
    }

    // ... rest of your existing methods


    private void ouvrirFenetreDetails(Hebergement hebergement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Agences/hotel_info_agence.fxml"));
            Parent root = loader.load();

            HotelInfo controller = loader.getController();
            controller.setHebergementDetails(hebergement);
            controller.setAgencyParent(agency_parent);

            agency_parent.setCenter(root);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Failed to load accommodation details: " + e.getMessage());
        }
    }

    @FXML
    public void switch_add(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Agences/hotel_add.fxml"));
            Parent root = loader.load();

            HotelAdd controller = loader.getController();
            // Pass the reference correctly
            controller.setAgencyParent(agency_parent);

            agency_parent.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Failed to load form: " + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}



