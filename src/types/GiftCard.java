package types;

/**
 * Created by Lance Judan on 1/21/2018
 */
public class GiftCard extends Card {
    private double amount;

    public GiftCard(String name, String number, double amount) {
        super(name, number);
        this.amount = amount;
    }

    //User can have many gift cards with for the same company ex: Amazon.
    //If that is the case then must compare using ID instead of name
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
        return name + " ($" + amount + ")";
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
