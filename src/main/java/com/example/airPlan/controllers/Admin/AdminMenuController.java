package com.example.airPlan.controllers.Admin;

import com.example.airPlan.models.Model;
import com.example.airPlan.views.AdminMenuOptions;
import com.example.airPlan.views.ClientMenuOptions;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

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
    private final Map<Button, AdminMenuOptions> buttonMenuMap = new HashMap<>();
    private List<Button> menuButtons;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initMenuButtons();
        addListeners();
        setActiveButton(flights_admin);
        Model.getInstance().getViewFactory().getAdminSelectedMenuItem().set(AdminMenuOptions.FLIGHT);
    }

    private void initMenuButtons() {
        buttonMenuMap.put(flights_admin, AdminMenuOptions.FLIGHT);
        buttonMenuMap.put(hotels_admin, AdminMenuOptions.HOTEL);

        menuButtons = Arrays.asList(
                flights_admin, hotels_admin
        );
    }

    private void addListeners() {
        for (Map.Entry<Button, AdminMenuOptions> entry : buttonMenuMap.entrySet()) {
            Button button = entry.getKey();
            AdminMenuOptions menuOption = entry.getValue();
            button.setOnAction(event -> {
                Model.getInstance().getViewFactory().getAdminSelectedMenuItem().set(menuOption);
                setActiveButton(button);
            });

        }
        logout_admin.setOnAction(event -> handleLogout());

    }

    private void setActiveButton(Button activeButton) {
        for (Button btn : menuButtons) {
            btn.getStyleClass().remove("active");
        }
        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }

    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Confirmer la déconnexion");
        alert.setContentText("Voulez-vous vraiment vous déconnecter ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) logout_admin.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showLoginView();
        }
    }
}