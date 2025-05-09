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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
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
}
