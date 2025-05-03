package com.example.airPlan.controllers.Client;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class HotelsController implements Initializable {
    public TableColumn colPhoto;
    public Text label_offer;
    public Button search_btn;
    public FontAwesomeIconView edit_btn;
    public Button cancel_btn;
    public ComboBox typeChambreComboBox;
    public FontAwesomeIconView add_btn;
    public Button reserve_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
