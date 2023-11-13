import java.io.*;
import java.util.*;

public class Player implements Runnable{
    private int playerNumber;
    public ArrayList<Card> hand;
    Deck leftDeck;
    Deck rightDeck; //having these be attributes of the player makes them easier to access
    static boolean gameWon = false; //by setting this value as static it can be updated globally
    static int winningPlayerNumber = 0;


    public Player(int playerNumber) {
        this.playerNumber = playerNumber;
        this.hand = new ArrayList<>();
    }

    public void addCardToHand(Card card) {
        hand.add(card);
    }


    public synchronized int getHandSize() {
        return hand.size();
    }

    public void setDecks(Deck left, Deck right) {
        leftDeck = left;
        rightDeck = right;
    }

    public Deck getLeftDeck() {
        return leftDeck;
    }

    public Deck getRightDeck() {
        return rightDeck;
    }

    public int getPlayerNumber() {
        return (playerNumber);
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    /*
    So fixed this. It basically just iterates through the hand, checking every card against the first one, since for a
    list to contain all the same elements, everything must be equal to the first element. If it ever finds something
    that isn't the same, it returns false
     */
    public boolean checkWinningHand() {
        for (Card card : hand) {
            int cardValue = card.getValue();
            if (cardValue != hand.get(0).getValue())
                return false;
        }
        return true;
    }


    public void run() {
        try {
            playGame();
        } catch (IOException anIOException) {
            anIOException.printStackTrace();
        }
    }

    public void playGame() throws IOException {
        //the gameWon value has been made an attribute, so it can be altered by the method which
        //receives a notification (in the form of an event) when another player wins
        String playerStr = ("player"+playerNumber+"_output.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(playerStr));
        while (!gameWon) {
            
                /*
                Runs a check to see if any player has won at the very start of the game. If they have, returns
                "Player # wins" and finishes the game
                */
                if (checkWinningHand()) {
                    System.out.println("Player " + playerNumber + " wins");
                    writer.write("Player " + playerNumber + " wins\n");
                    winningPlayerNumber = playerNumber;
                    gameWon = true;
                    break;
                }
       

                // Checks if the leftDeck is empty
                //sometimes the decks are empty but it doesn't *seem* to cause any problems 
                /*
                if (!leftDeck.getCardsInDeck().isEmpty()) {
                    drawAndDiscard(writer);      
                }
                */

                if (leftDeck.deckSize() == 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    drawAndDiscard(writer);
                }
            
                
        }
        

        // Print the final hands and exit messages for each player
        
        //System.out.println("player " + playerNumber + " final hand: " + hand);
        //System.out.println("player " + playerNumber + " exits");
        if (!checkWinningHand()) {
            writer.write("player " + winningPlayerNumber + " has informed player " + playerNumber + " that player " +winningPlayerNumber+" has won.\n");
        }
        writer.write("player " + playerNumber + " exits\n");
        writer.write("player " + playerNumber + " final hand: " + hand + "\n");
        
        
        endGame(writer);
        

    }


   
    private synchronized void drawAndDiscard(BufferedWriter writer) throws IOException {
        // Removes the 'top' card of the deck
        Card drawnCard = leftDeck.getCardsInDeck().remove(0);
        // Announces what the card 'drawn' is
        //System.out.println("player " + playerNumber + " draws a " + drawnCard.getValue() + " from deck " + leftDeck.getDeckNumber());
        writer.write("player " + playerNumber + " draws a " + drawnCard.getValue() + " from deck " + leftDeck.getDeckNumber() + "\n");
        // Then adds that card to hand
        addCardToHand(drawnCard);

        // Gets the players preferred card value (which is their player number)
        int preferredCardValue = playerNumber;
        /*
        Then iterates through their hand. When it finds a card that doesn't match their preferred
        value, it discards it to the deck to the right, and announces what card is discarded to
        what deck
        */
        boolean hasDiscarded = false;
        for (Card card : hand) {
            if (card.getValue() != preferredCardValue) {
                int discardIndex = hand.indexOf(card);
                Card discardedCard = hand.remove(discardIndex);
                rightDeck.addCardToDeck(discardedCard);
                hasDiscarded = true;
                //System.out.println("player " + playerNumber + " discards a " + discardedCard.getValue() + " to deck " + rightDeck.getDeckNumber());
                writer.write("player " + playerNumber + " discards a " + discardedCard.getValue() + " to deck " + rightDeck.getDeckNumber()+"\n");
                break;
            }
        }
        //to prevent a player ending up with 5 cards of their preferred denomination
        if (!hasDiscarded) {
            int discardIndex = 0;
            Card discardedCard = hand.remove(discardIndex);
            rightDeck.addCardToDeck(discardedCard); 
        }

        // Prints the current hand
        //System.out.print("player " + playerNumber + " current hand is " + hand);
        writer.write("player " + playerNumber + " current hand is " + hand + "\n");
        /**System.out.println("player" + playerNumber + "left deck is ");
        leftDeck.printDeck();
        System.out.println("player" + playerNumber + "right deck is ");
        rightDeck.printDeck();
        */
        //System.out.println();
    }

    private void endGame(BufferedWriter writer) {
        try {
            leftDeck.endGame();
            writer.close();
        } catch(IOException anIOException) {
            anIOException.printStackTrace();
        }
        
    }
}
