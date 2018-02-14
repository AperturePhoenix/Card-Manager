package models;

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

    public String getCVV() {
        return CVV;
    }

    public void setCVV(String CVV) {
        this.CVV = CVV;
    }

    @Override
    public void changeInfo(String[] info) {
        setNumber(info[InfoIndex.NUMBER]);
        setCVV(info[InfoIndex.CVV]);
        setExpiration(info[InfoIndex.EXPIRATION]);
    }

    @Override
    public boolean hasInfoChanged(String[] info) {
        boolean numberText = !number.equals(info[InfoIndex.NUMBER]);
        boolean cvvText = !CVV.equals(info[InfoIndex.CVV]);
        boolean expirationText  = !expiration.equals(info[InfoIndex.EXPIRATION]);
        return numberText || cvvText || expirationText;
    }
}
