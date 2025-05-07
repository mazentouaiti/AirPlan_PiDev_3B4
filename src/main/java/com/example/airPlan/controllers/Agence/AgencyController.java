package com.example.airPlan.controllers.Agence;

import com.example.airPlan.models.Model;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AgencyController implements Initializable {
    @FXML
    private BorderPane agency_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().getAgencySelectedMenuItem().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case FLIGHTS -> agency_parent.setCenter(Model.getInstance().getViewFactory().getAgencyFlightsView());
                case STATS -> agency_parent.setCenter(Model.getInstance().getViewFactory().getAgencyStaticView());
                // Add other cases for additional menu options
                default -> agency_parent.setCenter(Model.getInstance().getViewFactory().getAgencyFlightsView());
            }
        });
    }
}