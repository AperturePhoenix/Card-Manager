package types;

/**
 * Created by Lance Judan on 1/21/2018
 */
public class CreditCard extends Card {
    private String expiration;
    private String CVV;

    public CreditCard(String name, String number, String expiration, String CVV) {
        super(name, number);
        this.expiration = expiration;
        this.CVV = CVV;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }
}
