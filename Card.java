public class Card {
    public final int value;

    public Card(int value) {

        this.value = value;
    }

    public int getValue() {

        return value;
    }

    @Override public String toString() {

        return String.valueOf(value);
    }
}