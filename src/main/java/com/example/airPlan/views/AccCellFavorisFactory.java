package com.example.airPlan.views;

import com.example.airPlan.controllers.Client.FavorisCellController;
import com.example.airPlan.models.Hebergement;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import java.io.IOException;

public class AccCellFavorisFactory implements Callback<ListView<Hebergement>, ListCell<Hebergement>> {
    @Override
    public ListCell<Hebergement> call(ListView<Hebergement> param) {
        return new ListCell<Hebergement>() {
            @Override
            protected void updateItem(Hebergement hebergement, boolean empty) {
                super.updateItem(hebergement, empty);

                if (empty || hebergement == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/Fxml/Client/FavorisCell.fxml")
                        );
                        AnchorPane cellLayout = loader.load();
                        FavorisCellController controller = loader.getController();
                        controller.setHebergementData(hebergement);
                        setStyle("-fx-padding: 0 0 10 0; -fx-background-color: transparent;");

                        /*// Add hover effects
                        cellLayout.setOnMouseEntered(e -> {
                            cellLayout.setStyle("-fx-background-color: #f5f5f5;");
                        });

                        cellLayout.setOnMouseExited(e -> {
                            cellLayout.setStyle("-fx-background-color: transparent;");
                        });*/

                        setGraphic(cellLayout);
                    } catch (IOException e) {
                        e.printStackTrace();
                        setText(hebergement.getName()); // Fallback display
                    }
                }
            }
        };
    }
}