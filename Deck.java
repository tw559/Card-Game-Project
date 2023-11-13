import java.util.*;
import java.io.*; //will be needed when writes to files

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
    //for testing purposes only
    public void printDeck() {
        System.out.println(cards);
    }

    //for writing to file at end of game
    public void endGame() throws IOException {
        String deckStr = ("deck" + deckNumber + "_output.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(deckStr));
        writer.write("deck " + deckNumber + " contents at end of game: " + cards);

        writer.close();

    }
}
