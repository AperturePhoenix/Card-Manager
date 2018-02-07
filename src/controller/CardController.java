package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import types.Card;
import types.CreditCard;
import types.DebitCard;
import types.GiftCard;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Lance Judan on 1/25/2018
 */
public class CardController extends Controller {
    @FXML
    Label cardCompanyLabel, cardTypeLabel;
    @FXML
    TextField cardNumberTextField, CVVTextField, expirationTextField, amountTextField;
    @FXML
    ListView<Card> cardListView;
    @FXML
    Button addButton, removeButton, saveButton, cancelButton;
    @FXML
    HBox cardHBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cardListView.setItems(cardManager.getCards());
        cardListView.setOnMouseClicked(value -> updateInformation());

        cardNumberTextField.setOnKeyPressed(value -> updateOptions());
        CVVTextField.setOnKeyPressed(value -> updateOptions());
        expirationTextField.setOnKeyPressed(value -> updateOptions());
        amountTextField.setOnKeyPressed(value -> updateOptions());

        addButton.setOnAction((event -> {
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
        }));
        addButton.setTooltip(new Tooltip("Add new card"));
        removeButton.setOnAction(event -> cardManager.removeCard(getSelectedCard()));
        removeButton.setTooltip(new Tooltip("Remove selected card"));
        saveButton.setOnMouseClicked(event -> updateCard());
        cancelButton.setOnMouseClicked(event -> updateInformation());
    }

    private void updateInformation() {
        Card selectedCard = getSelectedCard();
        cardCompanyLabel.setText(selectedCard.getName());
        cardNumberTextField.setText(selectedCard.getNumber());
        if (selectedCard instanceof GiftCard) {
            cardHBox.setVisible(false);
            amountTextField.setVisible(true);
            cardTypeLabel.setText("Gift Card");
            amountTextField.setText("$" + ((GiftCard) selectedCard).getAmount());
        } else {
            cardHBox.setVisible(true);
            amountTextField.setVisible(false);
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
            String test = amountTextField.getText().replaceAll("[^0-9.]", "");
            System.out.println(test);
            double amount = Double.parseDouble(test);
            ((GiftCard)selectedCard).setAmount(amount);
        }
        else if (selectedCard instanceof CreditCard) {
            ((CreditCard)selectedCard).setCVV(CVVTextField.getText().trim());
            ((CreditCard)selectedCard).setExpiration(expirationTextField.getText().replaceAll("[0-9\\/]", "").trim());
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

    private void setEditButtonsDisabled(boolean isDisabled) {
        saveButton.setDisable(isDisabled);
        cancelButton.setDisable(isDisabled);
    }

    private Card getSelectedCard() {
        return cardListView.getSelectionModel().getSelectedItem();
    }
}
