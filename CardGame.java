import java.util.*;
import java.io.*;

public class CardGame {

    public static void main(String[] args) throws Exception{
        Scanner in = new Scanner(System.in);
        //System.out.println("Please enter number of players");
        //int n = Integer.parseInt(in.nextLine());
        int n = 3;
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
        for(Player player : players){
            int playerNumber = player.getPlayerNumber();
            Deck left = decks.get(playerNumber - 1);
            if (playerNumber < n) {
                Deck right = decks.get(playerNumber);
                player.setDecks(left, right);
            } else {
                Deck right = decks.get(0);
                player.setDecks(left, right);
            }
        }

        /*testing that assigning decks works
        for (Player p : players) {
            System.out.println("player: " + p.getPlayerNumber());
            System.out.println("left deck: " + p.getLeftDeck().getDeckNumber());
            System.out.println("right deck: " + p.getRightDeck().getDeckNumber());
        }*/
        boolean packRead = false;
        while (packRead == false) {
            //System.out.println("Please enter location of pack to load");
            //String file = in.nextLine();
            String file = "threeB.txt";
            try {
                remainingCards = attemptToReadPackFile(file,totalCards);
                packRead = true;
            }   catch(Exception e) {
                System.out.println("Invalid pack file");
            }
        }
        in.close();
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
        /**for (Player p : players) {
            for (Card c : p.getHand()) {
                System.out.println("player"+p.getPlayerNumber()+ "'s hand: " + c.getValue());
            }
        }*/

        //testing deck allocation
        /**for (Deck deck : decks) {
            for (Card card : deck.getCardsInDeck()) {
                System.out.println("deck"+deck.getDeckNumber()+ ": " + card.getValue());
            }
        }*/

        //now to actually start the player threads
        for (Player player : players) {
            Thread playerThread = new Thread(player);
            playerThread.start();
        }
        /*
        for (Player player : players) {
            System.out.println(player.getHand());
            System.out.println(player.checkWinningHand());
        }
        */


        int deckTotal = 0;
        for (Deck deck : decks) {
            deckTotal = deckTotal + deck.deckSize();
        }
        System.out.println(deckTotal);

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
