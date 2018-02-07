package controller;

import javafx.fxml.Initializable;
import managers.CardManager;

/**
 * Created by Lance Judan on 1/25/2018
 */
public abstract class Controller implements Initializable {
    protected CardManager cardManager;

    public Controller() {
        cardManager = CardManager.getInstance();
    }
}
