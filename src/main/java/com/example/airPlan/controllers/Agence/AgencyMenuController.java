package com.example.airPlan.controllers.Agence;

import com.example.airPlan.models.Model;
import com.example.airPlan.views.AdminMenuOptions;
import com.example.airPlan.views.AgencyMenuOptions;
import com.example.airPlan.views.ClientMenuOptions;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;
import java.util.ResourceBundle;

public class AgencyMenuController implements Initializable {
    @FXML
    public Button dash_agency;
    @FXML public Button flights_agency;
    @FXML public Button logout_btn;
    @FXML public Button report_btn;
    @FXML private Button acc_agency;
    @FXML private Button trans_agency;
    @FXML private Button stats_agency;
    @FXML private Button offers_agency;

    private final Map<Button, AgencyMenuOptions> buttonMenuMap = new HashMap<>();
    private List<Button> menuButtons;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        buttonMenuMap.put(dash_agency, AgencyMenuOptions.DASHBOARD);
        buttonMenuMap.put(flights_agency, AgencyMenuOptions.FLIGHTS);
//        buttonMenuMap.put(report_btn, AgencyMenuOptions.REPORTS);
//        buttonMenuMap.put(acc_agency, AgencyMenuOptions.ACCOUNT);
//        buttonMenuMap.put(trans_agency, AgencyMenuOptions.TRANSACTIONS);
        buttonMenuMap.put(stats_agency, AgencyMenuOptions.STATS);
//        buttonMenuMap.put(offers_agency, AgencyMenuOptions.OFFERS);
        addListeners();

        menuButtons = Arrays.asList(
                flights_agency , stats_agency
        );
        setActiveButton(flights_agency);
    }

    private void addListeners() {
//        dash_agency.setOnAction(event -> onDashboard());
        flights_agency.setOnAction(event -> onFlights());
        acc_agency.setOnAction(event -> onHotels());
        // Add listeners for other buttons
        logout_btn.setOnAction(event -> onLogout());

        // Add listeners using the buttonMenuMap
        buttonMenuMap.forEach((button, menuOption) -> {
            button.setOnAction(event -> {
                Model.getInstance().getViewFactory().getAgencySelectedMenuItem().set(menuOption);
                setActiveButton(button);
            });
        });
    }

//    private void onDashboard() {
//        Model.getInstance().getViewFactory().getAgencySelectedMenuItem().set(AgencyMenuOptions.DASHBOARD);
//    }

    private void onFlights() {
        Model.getInstance().getViewFactory().getAgencySelectedMenuItem().set(AgencyMenuOptions.FLIGHTS);
    }
    private void onHotels(){
        Model.getInstance().getViewFactory().getAgencySelectedMenuItem().set(AgencyMenuOptions.Hotels);
    }
    private void onStats(){
        Model.getInstance().getViewFactory().getAgencySelectedMenuItem().set(AgencyMenuOptions.STATS);
    }

    private void onLogout() {
        // Get the stage from any of the buttons
        Stage stage = (Stage) flights_agency.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showLoginView();
    }
    private void setActiveButton(Button activeButton) {
        for (Button btn : menuButtons) {
            btn.getStyleClass().remove("active");
        }
        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }
}