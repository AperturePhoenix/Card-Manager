package types;

import java.io.Serializable;

/**
 * Created by Lance Judan on 1/21/2018
 */
public abstract class Card implements Comparable<Card>, Serializable {
    protected String name;
    protected String number;

    public Card(String name, String number) {
        this.name = name;
        this.number = number;
    }

    @Override
    public int compareTo(Card card) {
        return name.compareTo(card.name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
