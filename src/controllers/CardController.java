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
import models.*;

import java.net.URL;
import java.util.ResourceBundle;

public class CardController extends Controller {
    //UI: JavaFX Nodes
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

        cardNumberTextField.setOnKeyReleased(event -> updateOptions());
        CVVTextField.setOnKeyReleased(event -> updateOptions());
        expirationTextField.setOnKeyReleased(event -> updateOptions());
        amountTextField.setOnKeyReleased(event -> updateOptions());

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
        getSelectedCard().changeInfo(getTextFields());
        updateInformation();
        setEditButtonsDisabled(true);
    }

    private boolean hasCardChanged() {
        return getSelectedCard().hasInfoChanged(getTextFields());
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

    private String[] getTextFields() {
        String[] info = new String[4];
        info[InfoIndex.NUMBER] = cardNumberTextField.getText().trim();
        info[InfoIndex.CVV] = CVVTextField.getText().trim();
        info[InfoIndex.EXPIRATION] = expirationTextField.getText().trim();
        info[InfoIndex.AMOUNT] = amountTextField.getText().trim();
        return info;
    }
}
