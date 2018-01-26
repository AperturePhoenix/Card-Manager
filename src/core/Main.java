package core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import managers.CardManager;
import managers.FileManager;

import java.util.Optional;

/**
 * Created by Lance Judan on 1/21/2018
 */
public class Main extends Application {
    CardManager cardManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        cardManager = new CardManager(getPassword());
    }

    private String getPassword() {
        Dialog<String> loginDialog = new Dialog<>();
        loginDialog.setTitle("Login");

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        loginDialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        grid.add(new Label("Password:"), 0, 0);
        grid.add(passwordField, 1, 0);

        Node loginButton = loginDialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        passwordField.textProperty().addListener(((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        }));

        loginDialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> passwordField.requestFocus());

        loginDialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return passwordField.getText();
            }
            return null;
        });

        Optional<String> password = loginDialog.showAndWait();
        if (password.isPresent()) {
            return password.get();
        }
        return null;
    }
}
