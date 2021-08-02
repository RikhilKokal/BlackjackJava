// Blackjack.java
// Text-Based Blackjack

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*; // Import all Java libraries


// public class Blackjack
// Main class of the Blackjack program
//
public class Blackjack {
    public static void main(String[] args) throws InterruptedException, IOException {
        Gameplay blackjackGame = new Gameplay();

        blackjackGame.loadSave();

        blackjackGame.howManyDecks();
        // While player still has chips
        while (blackjackGame.chips > 0) {
            blackjackGame.cardSetUp();

            blackjackGame.reset();

            blackjackGame.bet();

            // If player would like to exit, break out of while (chips > 0) loop
            if (blackjackGame.exit) {
                break;
            }

            blackjackGame.playerTotal = blackjackGame.checkIfAce(blackjackGame.playerCards, blackjackGame.playerTotal);
            blackjackGame.showHands(blackjackGame.playerCards, "Player");
            System.out.println("Dealer's cards: Hidden Card, " + blackjackGame.dealerCards.get(0));
            System.out.println("Dealer's total: " + blackjackGame.dealerTotal);
            System.out.println();

            if (blackjackGame.playerTotal == 21) {
                blackjackGame.natural();
                continue;
            }

            if (blackjackGame.insurance()) {
                continue;
            }

            blackjackGame.playerTotal = blackjackGame.playerTurn(blackjackGame.playerCards, blackjackGame.playerTotal);

            if (blackjackGame.split) {
                blackjackGame.showHands(blackjackGame.playerCards, "Player");
                blackjackGame.playerTotal = blackjackGame.playerTurn(blackjackGame.playerCards, blackjackGame.playerTotal);
                blackjackGame.showHands(blackjackGame.playerCardsSplit, "Player");
                blackjackGame.playerTotalSplit = blackjackGame.playerTurn(blackjackGame.playerCardsSplit, blackjackGame.playerTotalSplit);
            }

            Thread.sleep(1000);

            blackjackGame.dealerCards.add(0, blackjackGame.allCards.get(1));
            blackjackGame.dealerTotal += blackjackGame.dealerCards.get(0).value;

            blackjackGame.dealerTotal = blackjackGame.checkIfAce(blackjackGame.dealerCards, blackjackGame.dealerTotal);

            if (!blackjackGame.split) {
                blackjackGame.showHands(blackjackGame.playerCards, "Player");
            } else {
                blackjackGame.showHands(blackjackGame.playerCards, "Player Hand #1");
                blackjackGame.showHands(blackjackGame.playerCardsSplit, "Player Hand #2");
            }

            blackjackGame.showHands(blackjackGame.dealerCards, "Dealer");

            // Dealer turn
            blackjackGame.dealerTurn();

            blackjackGame.compareHands(blackjackGame.playerTotal);
            if (blackjackGame.split) {
                blackjackGame.compareHands(blackjackGame.playerTotalSplit);
            }

        } // end: while (chips > 0)

        if (blackjackGame.chips == 0) {
            System.out.println("You have no more chips.");
            if (blackjackGame.loadUsername) {
                blackjackGame.allScores.remove(blackjackGame.username);
                blackjackGame.scoresFile.delete();
                File scoresFile = new File("BlackjackScores.txt");
                scoresFile.createNewFile();
                FileWriter write = new FileWriter(scoresFile);
                for (Map.Entry<String, Integer> key : blackjackGame.allScores.entrySet()) {
                    String encodedData = Base64.getEncoder().encodeToString((key.getKey() + ":" + key.getValue()).getBytes());
                    write.write(encodedData + "\n");
                }
                write.close();
            }
        } else {
            blackjackGame.saveScore();
        }
        System.out.println("Thank you for playing!");
    }
//
}
// end: public class Blackjack