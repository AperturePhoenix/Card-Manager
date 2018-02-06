package types;

/**
 * Created by Lance Judan on 1/21/2018
 */
public class GiftCard extends Card {
    private int id;
    private double amount;

    public GiftCard(String name, String number, int id, double amount) {
        super(name, number);
        this.id = id;
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
            return id - ((GiftCard) card).id;
    }

    @Override
    public String toString() {
        return name + " ($" + amount + ")";
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
