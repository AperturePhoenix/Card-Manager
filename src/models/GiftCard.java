package models;

/**
 * Created by Lance Judan on 1/21/2018
 */
public class GiftCard extends Card {
    private double amount;

    public GiftCard(String name, String number, String amount) {
        super(name, number);
        setAmount(amount);
    }

    //User can have many gift cards with for the same company. If that is the case then must compare using amount
    //remaining instead of company name
    @Override
    public int compareTo(Card card) {
        int compareValue = super.compareTo(card);
        if (!(card instanceof GiftCard) || compareValue != 0)
            return compareValue;
        else
            return (int)(amount - ((GiftCard) card).amount);
    }

    @Override
    public String toString() {
        return getName() + " ($" + amount + ")";
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = parseAmount(amount);
    }

    //Regular Expression removes any characters that are not digits or '.'
    private double parseAmount(String amount) {
        return Double.parseDouble(amount.replaceAll("[^0-9.]", ""));
    }

    @Override
    public void changeInfo(String[] info) {
        setNumber(info[InfoIndex.NUMBER]);
        setAmount(info[InfoIndex.AMOUNT]);
    }

    @Override
    public boolean hasInfoChanged(String[] info) {
        boolean numberText = !number.equals(info[InfoIndex.NUMBER]);
        boolean amountText = !(amount == parseAmount(info[InfoIndex.AMOUNT]));
        return numberText || amountText;
    }
}
