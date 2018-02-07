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
    Button addButton, removeButton;
    @FXML
    HBox cardHBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cardListView.setItems(cardManager.getCards());
        cardListView.setOnMouseClicked(value -> updateInformation());

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
    }

    private void updateInformation() {
        Card selectedCard = getSelectedCard();
        cardCompanyLabel.setText(selectedCard.getName());
        cardNumberTextField.setText(selectedCard.getNumber());
        if (selectedCard instanceof GiftCard) {
            cardHBox.setVisible(false);
            amountTextField.setVisible(true);
            cardTypeLabel.setText("Gift Card");
            amountTextField.setText("$" + ((GiftCard)selectedCard).getAmount());
        }
        else {
            cardHBox.setVisible(true);
            amountTextField.setVisible(false);
            if (selectedCard instanceof CreditCard) {
                cardTypeLabel.setText("Credit Card");
                CVVTextField.setText(((CreditCard)selectedCard).getCVV());
                expirationTextField.setText(((CreditCard)selectedCard).getExpiration());
            }
            else {
                cardTypeLabel.setText("Debit Card");
                CVVTextField.setText(((DebitCard)selectedCard).getCVV());
                expirationTextField.setText(((DebitCard)selectedCard).getExpiration());
            }
        }
    }

    private Card getSelectedCard() {
        return cardListView.getSelectionModel().getSelectedItem();
    }
}
