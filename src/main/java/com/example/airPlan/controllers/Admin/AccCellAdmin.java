package com.example.airPlan.controllers.Admin;

import com.example.airPlan.models.Hebergement;
import com.example.airPlan.Services.ServiceHebergement;
import com.example.airPlan.Utiles.DBConnection;
import com.example.airPlan.views.AccCellAdminFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AccCellAdmin implements Initializable {

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
    @FXML
    private ListView<Hebergement> listHebergement;

    public void setListHebergement(ListView<Hebergement> listHebergement) {
        this.listHebergement = listHebergement;
    }
    private ObservableList<Hebergement> list = FXCollections.observableArrayList();



    @FXML
    private Button btnAccept;

    @FXML
    private Button btnReject;

    private ServiceHebergement service = new ServiceHebergement();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAccept.setOnAction(event -> {
            if (hebergement != null) {
                updateStatus("accepted");
                status_lbl.setText("accepted");
                btnAccept.setDisable(true);   // désactiver SEULEMENT Accept
                btnReject.setDisable(false);  // réactiver Reject si jamais
            }
        });

        btnReject.setOnAction(event -> {
            if (hebergement != null) {
                updateStatus("rejected");
                status_lbl.setText("rejected");
                btnReject.setDisable(true);   // désactiver SEULEMENT Reject
                btnAccept.setDisable(false);  // réactiver Accept si jamais
            }
        });


    }

// Plus besoin de disableButtons() ici car on gère directement bouton par bouton.

    @FXML
    private void acceptHebergement(ActionEvent event) {
        if (hebergement != null) {
            updateStatus("accepted");
            btnAccept.setDisable(true);
            btnReject.setDisable(false);
            status_lbl.setText("accepted");
            listHebergement.refresh();
        }
    }

    @FXML
    private void rejectHebergement(ActionEvent event) {
        if (hebergement != null) {
            updateStatus("refused");
            btnReject.setDisable(true);
            btnAccept.setDisable(false);
            status_lbl.setText("refused");
            listHebergement.refresh();
        }
    }

    private void updateStatus(String newStatus) {
        if (hebergement != null) {
            // Mettre à jour le statut local
            hebergement.setStatus(newStatus);

            // Créer un service de mise à jour
            ServiceHebergement service = new ServiceHebergement();

            // Toujours obtenir une nouvelle connexion à chaque appel de mise à jour
            try {
                // Vérification si la connexion est toujours ouverte
                if (DBConnection.getConnection() == null || DBConnection.getConnection().isClosed()) {
                    System.out.println("Connection is closed, reopening...");
                     // Méthode pour rouvrir la connexion, si nécessaire
                }

                // Mettre à jour la base de données
                service.updateStatus(hebergement.getId(), newStatus);

                // Optionnel : afficher un message de succès ou actualiser l'affichage
                System.out.println("Status updated to: " + newStatus);
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error updating status: " + e.getMessage());
            }
        }
    }




    private void loadHebergements() {
        try {
            List<Hebergement> hebergements = service.afficher();
            list = FXCollections.observableArrayList(hebergements);
            listHebergement.setCellFactory(param -> new AccCellAdminFactory());
            listHebergement.setItems(list);
        } catch (Exception e) {
            showErrorAlert("Erreur de chargement", "Échec du chargement des hébergements: " + e.getMessage());
        }
    }

    public void setHebergement(Hebergement hebergement) {
        this.hebergement = hebergement;
        updateAccData();
        if ("accepted".equalsIgnoreCase(hebergement.getStatus())) {
            btnAccept.setDisable(true);
            btnReject.setDisable(false);
        } else if ("rejected".equalsIgnoreCase(hebergement.getStatus())) {
            btnReject.setDisable(true);
            btnAccept.setDisable(false);
        } else {
            btnAccept.setDisable(false);
            btnReject.setDisable(false);
        }
    }

    private void updateAccData() {
        if (hebergement != null) {
            country_lbl.setText(hebergement.getCountry());
            status_lbl.setText(hebergement.getStatus());
            city_lbl.setText(hebergement.getCity());
            name_lbl.setText(hebergement.getName());
            type_lbl.setText(hebergement.getType());
            price_lbl.setText(String.format("TND %.2f", hebergement.getPricePerNight()));

            // Si déjà accepté ou refusé, désactiver les boutons
            boolean isDecided = hebergement.getStatus().equalsIgnoreCase("accepted") ||
                    hebergement.getStatus().equalsIgnoreCase("refused");
            btnAccept.setDisable(isDecided);
            btnReject.setDisable(isDecided);
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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
