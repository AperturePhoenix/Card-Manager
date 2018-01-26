package core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import managers.CardManager;

import java.util.Optional;

/**
 * Created by Lance Judan on 1/21/2018
 */
public class Main extends Application {
    private CardManager cardManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        cardManager = new CardManager();
    }

    @Override
    public void stop() {
        cardManager.exit();
    }
}
