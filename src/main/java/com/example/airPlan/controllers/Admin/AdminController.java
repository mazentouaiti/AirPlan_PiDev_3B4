package com.example.airPlan.controllers.Admin;

import com.example.airPlan.models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    public BorderPane admin_parent;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().getAdminSelectedMenuItem().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case FLIGHT -> admin_parent.setCenter(Model.getInstance().getViewFactory().getAdminFlightsView());
                case HOTEL -> admin_parent.setCenter(Model.getInstance().getViewFactory().getAdminHotelsView());
                default -> admin_parent.setCenter(Model.getInstance().getViewFactory().getAdminFlightsView());
            }
        });
    }
}
