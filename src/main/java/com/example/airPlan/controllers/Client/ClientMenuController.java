package com.example.airPlan.controllers.Client;

import com.example.airPlan.models.Model;
import com.example.airPlan.views.ClientMenuOptions;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class ClientMenuController implements Initializable {

    public Button flights_btn;
    public Button acc_btn;
    public Button trans_btn;
    public Button profile_btn;
    public Button logout_btn;
    public Button report_btn;
    public Button dash_btn;
    public Button assistance_user;
    public Button discover_user;

    private final Map<Button, ClientMenuOptions> buttonMenuMap = new HashMap<>();
    private List<Button> menuButtons;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initMenuButtons();
        addButtonListeners();
        setActiveButton(flights_btn); // default selection
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(ClientMenuOptions.DASHBOARD);
    }

    private void initMenuButtons() {
       //buttonMenuMap.put(dash_btn, ClientMenuOptions.DASHBOARD);
        buttonMenuMap.put(flights_btn, ClientMenuOptions.FLIGHT);
       // buttonMenuMap.put(acc_btn, ClientMenuOptions.HOTELS);
        //buttonMenuMap.put(trans_btn, ClientMenuOptions.TRANSPORT);
       // buttonMenuMap.put(profile_btn, ClientMenuOptions.PROFILE);
//        buttonMenuMap.put(report_btn, ClientMenuOptions.REPORT);
//        buttonMenuMap.put(assistance_user, ClientMenuOptions.ASSISTANCE);
//        buttonMenuMap.put(discover_user, ClientMenuOptions.DISCOVER);

        // only include nav-style buttons that need active styles
        menuButtons = Arrays.asList(
                dash_btn, flights_btn, acc_btn, trans_btn, profile_btn,
                report_btn, assistance_user, discover_user
        );
    }

    private void addButtonListeners() {
        for (Map.Entry<Button, ClientMenuOptions> entry : buttonMenuMap.entrySet()) {
            Button button = entry.getKey();
            ClientMenuOptions menuOption = entry.getValue();
            button.setOnAction(event -> {
                Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(menuOption);
                setActiveButton(button);
            });
        }

        logout_btn.setOnAction(event -> handleLogout());
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
            Stage stage = (Stage) logout_btn.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showLoginView();
        }
    }
}
