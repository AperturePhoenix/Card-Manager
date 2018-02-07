package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.CreditCard;
import models.DebitCard;
import models.GiftCard;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Lance Judan on 2/6/2018
 */
public class AddCardController extends Controller {
    @FXML
    TextField cardCompanyTextField, cardNumberTextField, cardExpirationTextField, optionTextField;
    @FXML
    ChoiceBox<String> cardTypeChoiceBox;
    @FXML
    Button addButton;
    @FXML
    Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Card Type Choice Box
        cardTypeChoiceBox.setItems(FXCollections.observableArrayList("Credit", "Debit", "Gift"));
        cardTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Gift")) {
                cardExpirationTextField.setVisible(false);
                optionTextField.setPromptText("Amount");
            } else {
                cardExpirationTextField.setVisible(true);
                optionTextField.setPromptText("CVV");
            }
        });

        addButton.setOnAction(event -> {
            if (isFormFilledOut()) {
                String company = cardCompanyTextField.getText();
                String number = cardNumberTextField.getText();
                String expiration = cardExpirationTextField.getText();
                String option = optionTextField.getText().trim();
                switch (cardTypeChoiceBox.getValue()) {
                    case "Credit":
                        cardManager.addCard(new CreditCard(company, number, expiration, option));
                        break;
                    case "Debit":
                        cardManager.addCard(new DebitCard(company, number, expiration, option));
                        break;
                    case "Gift":
                        double amount = Double.parseDouble(option.replaceAll("[^0-9.]", ""));
                        cardManager.addCard(new GiftCard(company, number, amount));
                        break;
                }
                ((Stage) addButton.getScene().getWindow()).close();
            }
        });

        cancelButton.setOnAction(event -> ((Stage) cancelButton.getScene().getWindow()).close());
    }

    private boolean isFormFilledOut() {
        boolean company = isFieldFilled(cardCompanyTextField);
        boolean number = isFieldFilled(cardNumberTextField);
        boolean expiration = isFieldFilled(cardExpirationTextField);
        boolean option = isFieldFilled(optionTextField);
        if (cardTypeChoiceBox.getSelectionModel().getSelectedItem().equals("Gift"))
            return company && number && option;
        else
            return company && number && expiration && option;
    }

    private boolean isFieldFilled(TextField field) {
        return !field.getText().isEmpty();
    }
}
