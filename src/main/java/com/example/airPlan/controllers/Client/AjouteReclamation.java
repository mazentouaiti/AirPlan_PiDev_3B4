package com.example.airPlan.controllers.Client;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.airPlan.models.reclamation;
import com.example.airPlan.Services.reclamationService;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

public class AjouteReclamation {

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField ratingField;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private TextField descriptionField;

    @FXML
    private Label errorLabel;

    private reclamationService reclamationService = new reclamationService();

    // For edit mode
    private boolean editMode = false;
    private int reclamationId = -1;

    @FXML
    public void initialize() {
        // Initialize type combo box with predefined types
        typeComboBox.setItems(FXCollections.observableArrayList(
                "flights",
                "Service",
                "transport"

        ));

        // Initialize status combo box
        statusComboBox.setItems(FXCollections.observableArrayList(
                "nouveau",
                "en cours",
                "résolu"
        ));

        // Set default value for status
        statusComboBox.setValue("nouveau");

        // Set default date to today
        datePicker.setValue(LocalDate.now());
    }

    public void setReclamationData(reclamation rec) {
        if (rec != null) {
            editMode = true;
            reclamationId = rec.getId();

            // Fill form fields with reclamation data
            typeComboBox.setValue(rec.getType());

            // Convert SQL date to LocalDate
            if (rec.getDatee() != null) {
                datePicker.setValue(rec.getDatee().toLocalDate());
            }

            descriptionField.setText(rec.getDescription());
            ratingField.setText(String.valueOf(rec.getNote()));
            statusComboBox.setValue(rec.getStatut());
        }
    }

    @FXML
    public void annuler(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Client/affichage_reclamation.fxml")));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page d'affichage des réclamations");
        }
    }

    @FXML
    public void save(ActionEvent actionEvent) {
        // Check if the required fields are filled
        if (typeComboBox.getValue() == null || descriptionField.getText().trim().isEmpty() || ratingField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs obligatoires");
            return;
        }

        try {
            // Validate rating is between 1 and 5
            int noteValue;
            try {
                noteValue = Integer.parseInt(ratingField.getText().trim());
                if (noteValue < 1 || noteValue > 5) {
                    showAlert(Alert.AlertType.WARNING, "Note invalide", "La note doit être entre 1 et 5");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Note invalide", "Veuillez entrer un nombre valide pour la note");
                return;
            }

            // Convert LocalDate to SQL Date
            Date sqlDate = null;
            if (datePicker.getValue() != null) {
                sqlDate = Date.valueOf(datePicker.getValue());
            } else {
                sqlDate = Date.valueOf(LocalDate.now());
            }

            // Create reclamation object
            reclamation rec = new reclamation(
                    editMode ? reclamationId : 0,
                    typeComboBox.getValue(),
                    sqlDate,
                    descriptionField.getText().trim(),
                    noteValue,
                    statusComboBox.getValue(),
                    1 // Default user ID - you might want to get this from session or other source
            );

            // Save or update reclamation
            if (editMode) {
                reclamationService.update(rec);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation mise à jour avec succès !");
            } else {
                reclamationService.add(rec);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation ajoutée avec succès !");
            }

            // Return to the reclamation list
            annuler(actionEvent);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}