package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.*;

import java.net.URL;
import java.util.ResourceBundle;

public class AddCardController extends Controller {
    //JavaFX Nodes
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
                String option = optionTextField.getText();
                switch (cardTypeChoiceBox.getValue()) {
                    case "Credit":
                        cardManager.addCard(new CreditCard(company, number, expiration, option));
                        break;
                    case "Debit":
                        cardManager.addCard(new DebitCard(company, number, expiration, option));
                        break;
                    case "Gift":
                        cardManager.addCard(new GiftCard(company, number, option));
                        break;
                }
                ((Stage) addButton.getScene().getWindow()).close();
            }
        });

        cancelButton.setOnAction(event -> ((Stage) cancelButton.getScene().getWindow()).close());
    }

    private boolean isFormFilledOut() {
        String selectedType = cardTypeChoiceBox.getSelectionModel().getSelectedItem();
        if (selectedType == null) {
            Dialog error = new Dialog();
            error.setTitle("Error");
            error.setContentText("Please select card type");
            ButtonType loginButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            error.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
            error.showAndWait();
            return false;
        }

        boolean company = isFieldFilled(cardCompanyTextField);
        boolean number = isFieldFilled(cardNumberTextField);
        boolean expiration = isFieldFilled(cardExpirationTextField);
        boolean option = isFieldFilled(optionTextField);
        if (selectedType.equals("Gift"))
            return company && number && option;
        else
            return company && number && expiration && option;
    }

    private boolean isFieldFilled(TextField field) {
        return !field.getText().isEmpty();
    }
}
