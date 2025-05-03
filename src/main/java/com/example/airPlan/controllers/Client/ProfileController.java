package com.example.airPlan.controllers.Client;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    
    public Label name_profile;
    public Label state;
    public Label nbr_flights;
    public Label cost_estimation;
    public Label hotel_nights;
    public Label top_destination;
    public TextField name_entry;
    public TextField age_entry;
    public TextField address_entry;
    public TextField email_entry;
    public TextField password_entry;
    public TextField cin_entry;
    public Button update_btn;
    public Button reset_btn;
    public Button delete_btn;
    public ImageView profile_img;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
