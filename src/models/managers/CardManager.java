package models.managers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import models.Card;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by Lance Judan on 1/25/2018
 */
public class CardManager {
    private static final String CARDS_FILE_NAME = "cards.ser";

    private static CardManager cardManagerInstance = null;
    private String password;
    private ObservableList<Card> cards;

    private CardManager() {
        this.password = getPassword(null);
        cards = loadCards();
    }

    public static CardManager getInstance() {
        if (cardManagerInstance == null) cardManagerInstance = new CardManager();
        return cardManagerInstance;
    }

    public void changePassword() {
        getPassword(null);
        FileManager.generateKeys();
    }

    public ObservableList<Card> getCards() {
        return cards;
    }

    public ObservableList<Card> getCards(Predicate<Card> predicate) {
        return cards.filtered(predicate);
    }

    public void addCard(Card card) {
        cards.add(findInsertionPoint(card), card);
    }

    private int binarySearch(Card card) {
        int low = 0, high = cards.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int compareValue = cards.get(mid).compareTo(card);
            if (compareValue < 0) low = mid + 1;
            else if (compareValue > 0) high = mid - 1;
            else return mid;
        }
        return -(low + 1);
    }

    private int findInsertionPoint(Card card) {
        int insertionPoint = binarySearch(card);
        if (insertionPoint < 0) insertionPoint = -(insertionPoint + 1);
        return insertionPoint;
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    private ObservableList<Card> loadCards() {
        Optional<ArrayList<Card>> optionalCard = null;
        while (optionalCard == null) {
            try {
                optionalCard = FileManager.loadFile(password, Card.class, CARDS_FILE_NAME);
            } catch (Exception e) {
                password = getPassword("Incorrect Password");
            }
        }
        return FXCollections.observableArrayList(optionalCard.orElse(new ArrayList<>()));
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

    public void exit() {
        ArrayList<Card> temp = new ArrayList<>(cards);
        FileManager.saveFile(password, temp, CARDS_FILE_NAME);

    }
}
