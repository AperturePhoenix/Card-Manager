package managers;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import types.Card;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by Lance Judan on 1/25/2018
 */
public class CardManager {
    private static final String CARDS_FILE_NAME = "cards.ser";

    private String password;
    private ArrayList<Card> cards;

    public CardManager() {
        this.password = getPassword(null);
        cards = loadCards();
    }

    public void changePassword() {
        getPassword(null);
        FileManager.generateKeys();
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    private ArrayList<Card> loadCards() {
        Optional<ArrayList<Card>> optionalCard = null;
        while (optionalCard == null) {
            try {
                optionalCard = FileManager.loadFile(password, Card.class, CARDS_FILE_NAME);
            } catch (Exception e) {
                getPassword("Incorrect Password");
            }
        }
        return optionalCard.orElse(new ArrayList<>());
    }

    public void exit() {
        FileManager.saveFile(password, cards, CARDS_FILE_NAME);
    }

    private String getPassword(String header) {
        Dialog<String> loginDialog = new Dialog<>();
        loginDialog.setTitle("Login");
        if (header != null) loginDialog.setHeaderText(header);

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

        passwordField.textProperty().addListener(((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty())));

        loginDialog.getDialogPane().setContent(grid);

        Platform.runLater(passwordField::requestFocus);

        loginDialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return passwordField.getText();
            }
            System.exit(0);
            return null;
        });

        Optional<String> password = loginDialog.showAndWait();
        return password.orElse(null);
    }
}
