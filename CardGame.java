import java.util.*;
import java.io.*;

public class CardGame {

    public static void playGame(List<Player> players, List<Deck> decks) {
        boolean gameWon = false;

        while (!gameWon) {
            for (Player player : players) {
                if (player.checkWinningHand()) {
                    System.out.println("Player " + player.getPlayerNumber() + " wins");
                    gameWon = true;
                    break;
                }

                // Draw a card from the left deck and discard to the right deck as a single atomic action
                synchronized (player.getLeftDeck()) {
                    synchronized (player.getRightDeck()) {
                        Deck leftDeck = player.getLeftDeck();
                        Deck rightDeck = player.getRightDeck();

                        if (!leftDeck.getCardsInDeck().isEmpty()) {
                            Card drawnCard = leftDeck.getCardsInDeck().remove(0);
                            System.out.println("player " + player.getPlayerNumber() + " draws a " +
                                    drawnCard.getValue() + " from deck " + leftDeck.getDeckNumber());
                            player.addCardToHand(drawnCard);

                            // Discard a card to the right deck
                            int preferredCardValue = player.getPlayerNumber();
                            for (Card card : player.getHand()) {
                                if (card.getValue() != preferredCardValue) {
                                    int discardIndex = player.getHand().indexOf(card);
                                    Card discardedCard = player.getHand().remove(discardIndex);
                                    rightDeck.addCardToDeck(discardedCard);
                                    System.out.println("player " + player.getPlayerNumber() + " discards a " +
                                            discardedCard.getValue() + " to deck " + rightDeck.getDeckNumber());
                                    break;
                                }
                            }
                            // Print the current hand
                            System.out.print("player " + player.getPlayerNumber() + " current hand is ");
                            for (Card card : player.getHand()) {
                                System.out.print(card.getValue() + " ");
                            }
                            System.out.println();

                            // Check if the player has won after the draw and discard
                            if (player.checkWinningHand()) {
                                System.out.println("Player " + player.getPlayerNumber() + " wins");
                                gameWon = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Print the final hands and exit messages for each player
        for (Player player : players) {
            System.out.println("player " + player.getPlayerNumber() + " final hand: " + player.getHand());
            System.out.println("player " + player.getPlayerNumber() + " exits");
        }

        // Print the contents of each deck at the end of the game
        for (Deck deck : decks) {
            System.out.println("deck" + deck.getDeckNumber() + " contents: " + deck.getCardsInDeck());
        }
    }
    public static void main(String[] args) throws Exception{
        Scanner in = new Scanner(System.in);
        System.out.println("Please enter number of players");
        int n = Integer.parseInt(in.nextLine());
        int totalCards = 8 * n;

        List<Deck> decks = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        List<Card> remainingCards = new ArrayList<>();


        for (int i = 1; i <= n; i++) {
            decks.add(new Deck(i));
        }

        for (int i = 1; i <= n; i++) {
            players.add(new Player(i));
        }

        //now assign decks to players
        for(Player p : players){
            int pn = p.getPlayerNumber();
            Deck left = decks.get(pn - 1);
            if (pn < n) {
                Deck right = decks.get(pn);
                p.setDecks(left, right);
            } else {
                Deck right = decks.get(0);
                p.setDecks(left, right);
            }
        }

        /*testing that assigning decks works
        for (Player p : players) {
            System.out.println("player: " + p.getPlayerNumber());
            System.out.println("left deck: " + p.getLeftDeck().getDeckNumber());
            System.out.println("right deck: " + p.getRightDeck().getDeckNumber());
        }*/

        System.out.println("Please enter location of pack to load");
        String file = in.nextLine();    
        in.close();    
        try {
            remainingCards = attemptToReadPackFile(file,totalCards);
        } catch(Exception e) {
            System.out.println("Invalid pack file");
        }

        //testing getting cards from file works
        /*for (Card c : remainingCards) {
            System.out.println(c.getValue());
        }*/

        Collections.shuffle(remainingCards);

        for (int i = 0; i < 4; i++) { //always four cards in a hand
            for (Player player : players) {
                Card card = remainingCards.remove(0);
                player.addCardToHand(card);
            }
        }

        int deckIndex = 0;
        for (Card card : remainingCards) {
            decks.get(deckIndex).addCardToDeck(card);
            deckIndex = (deckIndex + 1) % n;
        }
    

        //testing players have been allocated hands
        for (Player p : players) {
            for (Card c : p.getHand()) {
                System.out.println("player"+p.getPlayerNumber()+ "'s hand: " + c.getValue());
            }
        }

        //testing deck allocation
        for (Deck d : decks) {
            for (Card c : d.getCardsInDeck()) {
                System.out.println("deck"+d.getDeckNumber()+ ": " + c.getValue());
            }
        }

        //now to actually start the player threads
        for (Player p : players) {
            Thread playerThread = new Thread(p);
            playerThread.start();
        }

        //playGame(players, decks);

        for (Player player : players) {
            System.out.println(player.getHand());
            System.out.println(player.checkWinningHand());
        }
    }

    public static ArrayList<Card> attemptToReadPackFile(String file, int cardNum) throws Exception {
        ArrayList<Card> listOfCardsFromFile = new ArrayList<>();
        int denomination;
        BufferedReader fileIn = new BufferedReader(new FileReader(file));
        for (int i = 0; i < cardNum; i++) {
            denomination = Integer.parseInt(fileIn.readLine());
            listOfCardsFromFile.add(new Card(denomination));
        }
        fileIn.close();
        return listOfCardsFromFile;
    }


}
