package com.example.airPlan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Client/client_acc.fxml"));
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
