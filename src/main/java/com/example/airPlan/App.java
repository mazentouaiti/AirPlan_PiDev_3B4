package com.example.airPlan;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    private static HostServices hostServices;

    @Override
    public void start(Stage stage) throws IOException {
        hostServices = getHostServices();
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Login.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/images/logo.jpg"))));
        stage.show();
    }
    public static HostServices getHostServicesInstance() {
        return hostServices;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
