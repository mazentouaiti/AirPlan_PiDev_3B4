package com.example.airPlan.controllers;

import com.example.airPlan.models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


public class SignupController implements Initializable {
    public AnchorPane signup_parent;

    public TextField firstNameField;
    public TextField lastNameField;
    public TextField emailField;
    public TextField addressField;
    public PasswordField phoneField;
    public ComboBox roleComboBox;
    public Button signUpButton;
    public RadioButton Fcheck;
    public RadioButton Mcheck;
    public Button facebook;
    public Button google;
    public DatePicker BDField;
    public Button btnChooseFile;
    public PasswordField passField;
    public CheckBox agree_check;
    public Hyperlink linktologin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        linktologin.setOnAction(event -> {onlinkClicked();});

    }
    private void onlinkClicked() {
        Stage stage = (Stage) linktologin.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showLoginView();
    }
   /* @FXML
    private void handleBackToLogin (ActionEvent event) {
        // Logique pour revenir à la page login
        System.out.println("Retour à la page Login");
        // Utilise un mécanisme de changement de scène pour revenir à la page de login
    }

    @FXML
    private void handleLoginLink (ActionEvent event) {
        // Logique pour ouvrir la page Login
        System.out.println("Redirection vers la page de login");
        // Changer de scène ou ouvrir la fenêtre login
    }*/
    public void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un fichier");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            System.out.println("Fichier sélectionné : " + selectedFile.getAbsolutePath());
            // Tu peux ensuite afficher ou sauvegarder ce fichier
        }
    }

}
