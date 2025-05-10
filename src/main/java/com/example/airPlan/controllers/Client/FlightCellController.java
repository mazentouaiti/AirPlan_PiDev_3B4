package com.example.airPlan.controllers.Client;
import com.example.airPlan.models.FlightModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class FlightCellController implements Initializable {

    @FXML private Label des_lbl;
    @FXML private Label price_lbl;
    @FXML private Label status_lbl;
    @FXML private Label airline_lbl;
    @FXML private Label origin_lbl;
    @FXML private Label depart_lbl;
    @FXML private Button view_btn;

    private FlightModel flightModel;
    private FlightsController mainController;

    public void setMainController(FlightsController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        view_btn.setOnAction(event -> onViewClicked());
    }
    @FXML
    private void onViewClicked() {
        if (flightModel != null && mainController != null) {
            mainController.showReservationPanel(flightModel);
        } else {
            System.out.println("Error: " + (flightModel == null ? "No flight selected" : "Controller missing"));
        }
    }

    public void setFlight(FlightModel flightModel) {
        this.flightModel = flightModel;
        updateFlightData();
        view_btn.setDisable(false);
        view_btn.setTooltip(null);
    }

    private void updateFlightData() {
        if (flightModel != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            des_lbl.setText(flightModel.getDestination());
            origin_lbl.setText(flightModel.getOrigin());
            price_lbl.setText(String.format("€%.2f", flightModel.getPrice()));
            status_lbl.setText(flightModel.getStatus());
            depart_lbl.setText(dateFormat.format(flightModel.getDepartureDate()));
            airline_lbl.setText(flightModel.getAirline());

            // Add visual indicator for flight status
            if (!"approved".equals(flightModel.getAdminStatus())) {
                status_lbl.setStyle("-fx-text-fill: red;");
                status_lbl.setText(status_lbl.getText());
            } else {
                status_lbl.setStyle("-fx-text-fill: green;");
            }
            switch (flightModel.getStatus().toLowerCase()) {
                case "landed":
                    status_lbl.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    break;
                case "boarding":
                    status_lbl.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    break;
                case "delayed":
                    status_lbl.setStyle("-fx-text-fill: #FF5733; -fx-font-style: italic;");
                    break;
                case "cancelled":
                    status_lbl.setStyle("-fx-text-fill: red; -fx-strikethrough: true;");
                    break;
                case "scheduled":
                    status_lbl.setStyle("-fx-text-fill: blue;");
                    break;
                default:
                    status_lbl.setStyle("");
            }
        }
    }

    private void showErrorAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void setCancelButtonBehavior(Runnable onCancelAction) {
        view_btn.setText("Cancel");
        view_btn.setOnAction(event -> onCancelAction.run());
    }


}