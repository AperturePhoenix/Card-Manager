package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Lance Judan on 1/25/2018
 */
public class CardController extends Controller {
    @FXML ListView cardListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cardListView.setItems(FXCollections.observableArrayList("Test", "Test2", "Test3"));
    }
}
