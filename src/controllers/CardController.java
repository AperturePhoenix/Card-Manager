package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Card;
import models.CreditCard;
import models.DebitCard;
import models.GiftCard;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Lance Judan on 1/25/2018
 */
public class CardController extends Controller {
    //JavaFX Nodes
    @FXML
    Label cardCompanyLabel, cardTypeLabel;
    @FXML
    TextField searchTextField, cardNumberTextField, CVVTextField, expirationTextField, amountTextField;
    @FXML
    ListView<Card> cardListView;
    @FXML
    Button passwordButton, addButton, removeButton, saveButton, cancelButton;
    @FXML
    HBox cardHBox;
    @FXML
    VBox cardVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cardListView.setItems(cardManager.getCards());
        cardListView.setOnMouseClicked(value -> updateInformation());
        //Allows the user to manipulate the list using keyboard
        cardListView.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.DELETE) removeCard();
            if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) updateInformation();
        });

        searchTextField.setOnKeyReleased(event -> search());

        cardNumberTextField.setOnKeyPressed(event -> updateOptions());
        CVVTextField.setOnKeyPressed(event -> updateOptions());
        expirationTextField.setOnKeyPressed(event -> updateOptions());
        amountTextField.setOnKeyPressed(event -> updateOptions());

        passwordButton.setOnAction(event -> cardManager.changePassword());
        passwordButton.setTooltip(new Tooltip("Change the password"));
        addButton.setOnAction((event -> createNewCard()));
        addButton.setTooltip(new Tooltip("Add new card"));
        removeButton.setOnAction(event -> removeCard());
        removeButton.setTooltip(new Tooltip("Remove selected card"));
        saveButton.setOnMouseClicked(event -> updateCard());
        saveButton.setTooltip(new Tooltip("Save changes to the card"));
        cancelButton.setOnMouseClicked(event -> updateInformation());
        cancelButton.setTooltip(new Tooltip("Discard changes to the card"));
    }

    //Checks if the cards company name contains the search query or card number ends in the search query
    private void search() {
        String searchString = searchTextField.getText().toLowerCase();
        cardListView.setItems(cardManager.getCards(card -> card.getName().toLowerCase().contains(searchString) || card.getNumber().toLowerCase().endsWith(searchString)));
    }

    private void updateInformation() {
        Card selectedCard = getSelectedCard();
        if (selectedCard == null) {
            cardCompanyLabel.setText("Card Company");
            cardTypeLabel.setText("Card Type");
            cardNumberTextField.clear();
            CVVTextField.clear();
            expirationTextField.clear();
            amountTextField.clear();
            setEditButtonsDisabled(true);
            return;
        }
        cardCompanyLabel.setText(selectedCard.getName());
        cardNumberTextField.setText(selectedCard.getNumber());
        if (selectedCard instanceof GiftCard) {
            cardHBox.setVisible(false);
            cardVBox.setVisible(true);
            cardTypeLabel.setText("Gift Card");
            amountTextField.setText("$" + ((GiftCard) selectedCard).getAmount());
        } else {
            cardHBox.setVisible(true);
            cardVBox.setVisible(false);
            if (selectedCard instanceof CreditCard) {
                cardTypeLabel.setText("Credit Card");
                CVVTextField.setText(((CreditCard) selectedCard).getCVV());
                expirationTextField.setText(((CreditCard) selectedCard).getExpiration());
            } else {
                cardTypeLabel.setText("Debit Card");
                CVVTextField.setText(((DebitCard) selectedCard).getCVV());
                expirationTextField.setText(((DebitCard) selectedCard).getExpiration());
            }
        }
        setEditButtonsDisabled(true);
    }

    private void updateOptions() {
        if (hasCardChanged()) {
            setEditButtonsDisabled(false);
        } else {
            setEditButtonsDisabled(true);
        }
    }

    private void updateCard() {
        Card selectedCard = getSelectedCard();
        selectedCard.setNumber(cardNumberTextField.getText().trim());
        if (selectedCard instanceof GiftCard) {
            //Regular Expression removes any characters that are not digits or '.'
            String test = amountTextField.getText().replaceAll("[^0-9.]", "");
            System.out.println(test);
            double amount = Double.parseDouble(test);
            ((GiftCard) selectedCard).setAmount(amount);
        } else {
            String cvv = CVVTextField.getText().trim();
            //Regular Expression removes any characters that are not digits or '/'
            String expiration = expirationTextField.getText().replaceAll("[^0-9/]", "").trim();
            if (selectedCard instanceof CreditCard) {
                ((CreditCard) selectedCard).setCVV(cvv);
                ((CreditCard) selectedCard).setExpiration(expiration);
            } else {
                ((DebitCard) selectedCard).setCVV(cvv);
                ((DebitCard) selectedCard).setExpiration(expiration);
            }
        }
        updateInformation();
        setEditButtonsDisabled(true);
    }

    private boolean hasCardChanged() {
        Card selectedCard = getSelectedCard();
        boolean number = !cardNumberTextField.getText().equals(selectedCard.getNumber());
        if (selectedCard instanceof GiftCard) {
            boolean amount = !amountTextField.getText().equals("$" + ((GiftCard) selectedCard).getAmount());
            return number || amount;
        } else if (selectedCard instanceof CreditCard) {
            boolean cvv = !CVVTextField.getText().equals(((CreditCard) selectedCard).getCVV());
            boolean expiration = !expirationTextField.getText().equals(((CreditCard) selectedCard).getExpiration());
            return number || cvv || expiration;
        } else {
            boolean cvv = !CVVTextField.getText().equals(((DebitCard) selectedCard).getCVV());
            boolean expiration = !expirationTextField.getText().equals(((DebitCard) selectedCard).getExpiration());
            return number || cvv || expiration;
        }
    }

    private void createNewCard() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddCardView.fxml"));
        loader.setRoot(new GridPane());
        Parent root = new GridPane();
        try {
            root = loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stage stage = new Stage();
        stage.setTitle("Add Card");
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    private void removeCard() {
        cardManager.removeCard(getSelectedCard());
    }

    private void setEditButtonsDisabled(boolean isDisabled) {
        saveButton.setDisable(isDisabled);
        cancelButton.setDisable(isDisabled);
    }

    private Card getSelectedCard() {
        return cardListView.getSelectionModel().getSelectedItem();
    }
}
