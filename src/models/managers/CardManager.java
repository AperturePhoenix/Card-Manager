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

public class CardManager {
    private static final String CARDS_FILE_NAME = "cards.ser";
    private static CardManager cardManagerInstance = null;

    private String password;
    private ObservableList<Card> cards;

    public static CardManager getInstance() {
        if (cardManagerInstance == null) cardManagerInstance = new CardManager();
        return cardManagerInstance;
    }

    private CardManager() {
        this.password = getPassword(PasswordType.NORMAL);
        cards = loadCards();
    }

    public ObservableList<Card> getCards() {
        return cards;
    }

    public ObservableList<Card> getCards(Predicate<Card> filter) {
        return cards.filtered(filter);
    }

    public void addCard(Card card) {
        cards.add(findInsertionPoint(card), card);
    }

    //Uses a binary search algorithm to find where the Card should be inserted into the list
    private int findInsertionPoint(Card card) {
        int low = 0, high = cards.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int compareValue = cards.get(mid).compareTo(card);
            if (compareValue < 0) low = mid + 1;
            else if (compareValue > 0) high = mid - 1;
            else return mid;
        }
        return low;
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
                password = getPassword(PasswordType.INCORRECT);
            }
        }
        return FXCollections.observableArrayList(optionalCard.orElse(new ArrayList<>()));
    }

    public void changePassword() {
        password = getPassword(PasswordType.CHANGE);
        FileManager.generateKeys();
    }

    private String getPassword(PasswordType passwordType) {
        Dialog<String> loginDialog = new Dialog<>();
        String title = passwordType.getTitle();
        String header = passwordType.getHeader();
        String buttonText = passwordType.getButtonText();

        loginDialog.setTitle(title);
        if (header != null) loginDialog.setHeaderText(header);

        ButtonType loginButtonType = new ButtonType(buttonText, ButtonBar.ButtonData.OK_DONE);
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

    private enum PasswordType {
        NORMAL("Login", null, "Login"),
        INCORRECT("Error", "Incorrect Password", "Login"),
        CHANGE("Change Password", "Enter New Password", "Change");

        private final String title, header, buttonText;

        PasswordType(String title, String header, String buttonText) {
            this.title = title;
            this.header = header;
            this.buttonText = buttonText;
        }

        public String getTitle() {
            return title;
        }

        public String getHeader() {
            return header;
        }

        public String getButtonText() {
            return buttonText;
        }
    }
}
