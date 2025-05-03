package com.example.airPlan.views;

import com.example.airPlan.controllers.Client.FlightCellController;
import com.example.airPlan.models.FlightModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class FlightCellFactory extends ListCell<FlightModel> {

    @Override
    protected void updateItem(FlightModel flight, boolean empty) {
        super.updateItem(flight, empty);

        if (empty || flight == null) {
            setText(null);
            setGraphic(null);
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/FlightCell.fxml"));
                Node cellView = loader.load();
                FlightCellController controller = loader.getController();
                controller.setFlight(flight);
                setGraphic(cellView);
            } catch (IOException e) {
                setText("Flight: " + flight.getFlightNumber());
                e.printStackTrace();
            }
        }
    }
}