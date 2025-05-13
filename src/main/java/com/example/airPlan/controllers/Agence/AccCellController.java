package com.example.airPlan.controllers.Agence;

import com.example.airPlan.models.Hebergement;
import com.example.airPlan.Services.ServiceHebergement;
import com.example.airPlan.views.AccCellFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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

public class AccCellController implements Initializable {

    @FXML
    private Label price_lbl;
    @FXML
    private Label type_lbl;
    @FXML
    private Label status_lbl;
    @FXML
    private Label name_lbl;
    @FXML
    private Label city_lbl;
    @FXML
    private Label country_lbl;
    private Hebergement hebergement;
    private ListView<Hebergement> listHebergement;
    private FilteredList<Hebergement> filteredList;

    // Pas @FXML !
    public void setListHebergement(ListView<Hebergement> listHebergement) {
        this.listHebergement = listHebergement;
    }

    private ObservableList<Hebergement> list;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnEdit;
    private ServiceHebergement service = new ServiceHebergement(); // À ajouter
    private BorderPane agencyParent;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {



        // Charger les hébergements dans la ListView


        btnEdit.setOnAction(event -> {
            if (hebergement != null) {
                openModifyHebergementWindow(hebergement);
            }
        });

        btnDelete.setOnAction(event -> {
            if (hebergement != null) {
                deleteHebergement(hebergement);
                setListHebergement(listHebergement);
            }
        });
    }
    private void openModifyHebergementWindow(Hebergement hebergement) {
        System.out.println("Opening modification window for: " + hebergement.getName());

        try {
            // 1. Load the FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Agences/hotel_add.fxml"));
            Parent modifyView = loader.load();

            // 2. Get controller and initialize data
            HotelAdd controller = loader.getController();
            controller.initData(hebergement);

            // 3. Verify and update the center content
            if (agencyParent == null) {
                // Fallback: Get BorderPane from current scene if agencyParent wasn't set
                Node source = (Node) listHebergement; // Or any other node in the scene
                BorderPane root = (BorderPane) source.getScene().getRoot();

                if (root != null) {
                    System.out.println("Using scene root as fallback BorderPane");
                    root.setCenter(modifyView);
                } else {
                    throw new IllegalStateException("Could not find BorderPane parent");
                }
            } else {
                System.out.println("Using agencyParent BorderPane");
                agencyParent.setCenter(modifyView);
            }

        } catch (IOException e) {
            System.err.println("FXML Loading Error:");
            e.printStackTrace();
            showErrorAlert("Loading Error", "Failed to load modification view: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected Error:");
            e.printStackTrace();
            showErrorAlert("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void deleteHebergement(Hebergement selectedHebergement) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Delete Accommodation");
        alert.setContentText("Are you sure you want to delete this accommodation?");


        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Supprimer de la base de données
                service.supprimer(selectedHebergement.getId());

                if (listHebergement != null) {
                    // Create a new modifiable list from the current items
                    ObservableList<Hebergement> currentItems = FXCollections.observableArrayList(listHebergement.getItems());

                    // Remove the item from the modifiable list
                    currentItems.remove(selectedHebergement);

                    // Set the updated list back to the ListView
                    listHebergement.setItems(currentItems);

                    // If you're using a filtered list, update it as well
                    if (filteredList != null) {
                        filteredList = new FilteredList<>(currentItems, p -> true);
                        listHebergement.setItems(filteredList);
                    }
                } else {
                    System.out.println("⚠️ listHebergement est null, impossible de rafraîchir.");
                }
            }
        });
    }

    private void loadHebergements() {
        try {
            List<Hebergement> hebergements = service.afficher();

            if (list == null) {
                // Première fois : on crée l'ObservableList et on setup la ListView
                list = FXCollections.observableArrayList(hebergements);
                filteredList = new FilteredList<>(list, p -> true);
                listHebergement.setItems(filteredList);
                listHebergement.setCellFactory(param -> new AccCellFactory());
            } else {
                // Déjà initialisé : on met juste à jour les données
                list.setAll(hebergements);
            }
        } catch (Exception e) {
            showErrorAlert("Loading Error", "Failed to load hebergements: " + e.getMessage());
        }
    }

    public void setHebergement(Hebergement hebergement) {
        this.hebergement = hebergement;
        updateAccData();
    }

    private void updateAccData() {
        if (hebergement != null) {
            country_lbl.setText(hebergement.getCountry());
            status_lbl.setText(hebergement.getStatus());
            city_lbl.setText(hebergement.getCity());
            name_lbl.setText(hebergement.getName());
            type_lbl.setText(hebergement.getType());
            price_lbl.setText(String.format("TND %.2f", hebergement.getPricePerNight()));
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(
                Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
