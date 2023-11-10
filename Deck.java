import java.util.*;

public class Deck {

    private ArrayList<Card> cards;
    private int deckNumber;

    public synchronized void shuffle() {
        Collections.shuffle(cards);
    }

    public Deck(int deckNumber) {
        this.cards = new ArrayList<>();
        this.deckNumber = deckNumber;
    }

    public synchronized Card drawCard() {
        if (!cards.isEmpty()) {
            return cards.remove(0);
        }
        return null;
    }

    public synchronized void addCardToDeck(Card card) {
        cards.add(card);
    }

    public synchronized int deckSize() {
        return cards.size();
    }

    public int getDeckNumber() {
        return deckNumber;
    }

    public ArrayList<Card> getCardsInDeck() {
        return cards;
    }

    //TODO: implement run-time functionality for decks i.e. writing to files at end of game
}
