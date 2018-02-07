package core;

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
    //Window variables
    private static final String TITLE = "Card Manager";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader cardLoader = new FXMLLoader(getClass().getResource("/views/CardView.fxml"));
        cardLoader.setRoot(new SplitPane());
        Parent root = cardLoader.load();

        primaryStage.setTitle(TITLE);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void stop() {
        CardManager.getInstance().exit();
    }
}
