package com.example.airPlan.controllers.Client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.airPlan.models.reclamation;
import com.example.airPlan.Services.reclamationService;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class AffichageReclamation {
    @FXML
    private ListView<reclamation> listViewRec;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterComboBox;


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


    private void configureCellFactory() {
        listViewRec.setCellFactory(param -> new ListCell<reclamation>() {
            @Override
            protected void updateItem(reclamation reclamation, boolean empty) {
                super.updateItem(reclamation, empty);
                if (empty || reclamation == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create custom cell layout
                    VBox container = new VBox(5);
                    HBox firstLine = new HBox(10);
                    HBox secondLine = new HBox(10);
                    HBox buttonBox = new HBox(10);

                    // Create labels
                    Label idLabel = new Label("ID: " + reclamation.getId());
                    Label typeLabel = new Label("Type: " + reclamation.getType());
                    Label dateLabel = new Label("Date: " + reclamation.getDatee());
                    Label descriptionLabel = new Label("Description: " + reclamation.getDescription());
                    Label ratingLabel = new Label("Rating: " + reclamation.getNote());
                    Label statusLabel = new Label("Status: " + reclamation.getStatut());

                    // Style the labels
                    descriptionLabel.setStyle("-fx-font-weight: bold;");
                    statusLabel.setStyle("-fx-text-fill: " + (reclamation.getStatut().equalsIgnoreCase("resolved") ? "green" : "red") + ";");

                    // Create buttons
                    Button editButton = new Button("Edit");
                    Button deleteButton = new Button("Delete");

                    // Style buttons
                    editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                    // Set button actions
                    editButton.setOnAction(event -> openEditForm(reclamation));
                    deleteButton.setOnAction(event -> deleteReclamation(reclamation));

                    // Add components to containers
                    firstLine.getChildren().addAll(idLabel, typeLabel);
                    secondLine.getChildren().addAll(dateLabel, descriptionLabel);
                    buttonBox.getChildren().addAll(ratingLabel, statusLabel, editButton, deleteButton);
                    container.getChildren().addAll(firstLine, secondLine, buttonBox);

                    setGraphic(container);
                }
            }
        });
    }

    private void openEditForm(reclamation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/UpdateReclamation.fxml"));
            Parent root = loader.load();

            UpdateReclamation controller = loader.getController();
            controller.setReclamationData(reclamation); // Pass the reclamation object

            Stage stage = (Stage) listViewRec.getScene().getWindow(); // Reuse current window
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Reclamation - ID: " + reclamation.getId()); // Keep title
            stage.show();

            // Refresh list after window closes (preserved from original)
            stage.setOnHidden(event -> loadReclamations());
        } catch (IOException e) {
            // Aligned with feedback's error handling style
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load edit form: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
    private void deleteReclamation(reclamation reclamation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Reclamation ID: " + reclamation.getId());
        alert.setContentText("Are you sure you want to delete this reclamation?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    reclamationService.delete(reclamation.getId());
                    loadReclamations();
                    showAlert("Success", "Reclamation ID: " + reclamation.getId() + " deleted successfully");
                } catch (Exception e) {
                    showAlert("Error", "Failed to delete reclamation: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void handleAddButton() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Client/AjouteReclamation.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add New Reclamation");
            stage.show();

            stage.setOnHidden(event -> loadReclamations());
        } catch (IOException e) {
            showAlert("Error", "Failed to load add form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}