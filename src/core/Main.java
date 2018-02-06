package core;

import controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import managers.CardManager;

/**
 * Created by Lance Judan on 1/21/2018
 */
public class Main extends Application {
    private CardManager cardManager;

    //Window variables
    private static final String TITLE = "Card Manager";
    private static final int PREFERED_WIDTH = 800, PREFERED_HEIGHT = 550;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        cardManager = new CardManager();

        FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("/views/CardView.fxml"));
        cardLoader.setRoot(new SplitPane());
        Parent root = cardLoader.load();
        ((Controller) cardLoader.getController()).setCardManager(cardManager);

        primaryStage.setTitle(TITLE);
        primaryStage.setScene(new Scene(root, PREFERED_WIDTH, PREFERED_HEIGHT));
        primaryStage.show();
    }

    @Override
    public void stop() {
        cardManager.exit();
    }
}
