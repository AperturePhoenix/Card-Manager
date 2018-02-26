package controllers;

import javafx.fxml.Initializable;
import models.managers.CardManager;

abstract class Controller implements Initializable {
    CardManager cardManager;

    Controller() {
        cardManager = CardManager.getInstance();
    }
}
