package controllers;

import javafx.fxml.Initializable;
import models.managers.CardManager;

/**
 * Created by Lance Judan on 1/25/2018
 */
abstract class Controller implements Initializable {
    CardManager cardManager;

    Controller() {
        cardManager = CardManager.getInstance();
    }
}
