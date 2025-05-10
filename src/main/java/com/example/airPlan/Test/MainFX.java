
package com.example.airPlan.Test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("Loading FXML...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/affichage_reclamation.fxml"));
            Parent root = loader.load();
            System.out.println("FXML Loaded Successfully!");

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("hedi");
            primaryStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}