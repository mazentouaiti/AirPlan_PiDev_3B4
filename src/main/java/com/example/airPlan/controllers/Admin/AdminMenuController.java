package com.example.airPlan.controllers.Admin;

import com.example.airPlan.models.Model;
import com.example.airPlan.views.AdminMenuOptions;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminMenuController implements Initializable {
    public Button dash_admin;
    public Button flights_admin;
    public Button hotels_admin;
    public Button transport_admin;
    public Button offers_admin;
    public Button clients_admin;
    public Button agences_admin;
    public Button feed_admin;
    public Button stats_admin;
    public Button chats_admin;
    public Button logout_admin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
    }

    private void addListeners() {
        logout_admin.setOnAction(actionEvent -> onLogout());
        flights_admin.setOnAction(actionEvent -> onFlight_admin());

    }
    private void onFlight_admin() {
        Model.getInstance().getViewFactory().getAdminSelectedMenuItem().set(AdminMenuOptions.FLIGHT);
    }
    private void onLogout() {
        Stage stage = (Stage) logout_admin.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showLoginView();
    }
}
