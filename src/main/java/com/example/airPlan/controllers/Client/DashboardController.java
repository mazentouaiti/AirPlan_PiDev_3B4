package com.example.airPlan.controllers.Client;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;


public class DashboardController implements Initializable {
    public Text user_name;
    public Label login_date;
    public Label destination;
    public Label date_dest;
    public Label next_destination;
    public Label next_date;
    public Label budget;
    public ProgressBar progression;
    public ListView notification_list;
    public ListView deals_list;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}
}
