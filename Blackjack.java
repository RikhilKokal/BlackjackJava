// Blackjack.java
// Text-Based Blackjack

import java.util.*; // Import all Java libraries


// public class Blackjack
// Main class of the Blackjack program
//
public class Blackjack {
    static Scanner input = new Scanner(System.in);
    static boolean exit = false;
    static int numOfDecks =    1;
    static int chips = 1000;
    static int bet = 0;
    static int cardCount = 4;
    static boolean split = false;
    static ArrayList<Card> allCards = new ArrayList<>();
    static ArrayList<Card> playerCards = new ArrayList<>();
    static ArrayList<Card> playerCardsSplit = new ArrayList<>();
    static ArrayList<Card> dealerCards = new ArrayList<>();
    static int playerTotal = 0;
    static int dealerTotal = 0;
    static int playerTotalSplit = 0;

    public static void howManyDecks() {
        while (true) {
            System.out.println("How many decks of cards would you like to play with? (1-8)");
            String decksNum = input.next();
            try {
                numOfDecks = Integer.parseInt(decksNum);
            } catch (NumberFormatException e) {
                System.err.println("That is not a number of decks that you can play with.");
                continue;
            }

            if (numOfDecks >= 1 && numOfDecks <= 8) {
                break;
            } else {
                System.err.println("That is not a number of decks that you can play with.");
            }
        }
    }

    public static void cardSetUp() {
        // Variable declaration
        String name; // Name of cards
        String suits; // Suits of cards

        allCards.clear();

        // Loops through deck of 52 cards
        for (int deck = 1; deck <= numOfDecks; deck++) {
            for (int card = 1; card <= 52; card++) {
                // Assigns name to card
                name = switch (card % 13) {
                    case 1 -> "Ace";
                    case 11 -> "Jack";
                    case 12 -> "Queen";
                    case 0 -> "King";
                    default -> Integer.toString(card % 13);
                };

                // Assigns suits to card
                if (card <= 13) {
                    suits = "Clubs";
                } else if (card <= 26) {
                    suits = "Diamonds";
                } else if (card <= 39) {
                    suits = "Hearts";
                } else {
                    suits = "Spades";
                }

                Card newCard = new Card(name, suits); // Creates card
                Blackjack.allCards.add(newCard); // Adds newly-created card to allCards ArrayList
            }
        }
    }

    public static void reset() {
        Collections.shuffle(allCards);
        playerCards.clear();
        playerCardsSplit.clear();
        playerTotal = 0;
        dealerCards.clear();
        dealerTotal = 0;
        playerCards.add(allCards.get(0)); // Add the first card in the deck to playerCards
        playerCards.add(allCards.get(2)); // Add the third card in the deck to playerCards
        dealerCards.add(allCards.get(3)); // Add the fourth card in the deck to dealerCards
        playerTotal += playerCards.get(0).value; // Add the value of the first card in playerCards to playerTotal
        playerTotal += playerCards.get(1).value; // Add the value of the second card in playerCards to playerTotal
        dealerTotal += dealerCards.get(0).value; // Add the value of the first card in dealerCards to dealerTotal
    }

    public static void showHands(ArrayList<Card> hand, String playerOrDealer) {
        int total = 0;
        System.out.print(playerOrDealer + "'s hand: ");
        for (int card = 0; card < hand.size() - 1; card++) {
            System.out.print(hand.get(card) + ", ");
            total += hand.get(card).value;
        }
        System.out.println(hand.get(hand.size() - 1));
        total += hand.get(hand.size() - 1).value;
        System.out.println(playerOrDealer + "'s total: " + total);
        System.out.println();
    }

    public static void bet () {
        while (true) {
            System.out.println("You have " + chips + " chips. Please enter the number of chips you want to bet or [E]xit.");
            String betOrExit = input.next(); // Ask for input
            // If player would like to exit
            if (betOrExit.equalsIgnoreCase("E")) {
                Blackjack.exit = true;
                break;
            } else {
                // Try converting input into integer. If not possible, return error
                try {
                    bet = Integer.parseInt(betOrExit);
                } catch (NumberFormatException e) {
                    System.err.println("That is not a number of chips that you can bet.");
                    continue;
                }
            }
            // Make sure the player entered a valid number of chips to bet
            if (bet < 0 || bet == 0) {
                System.err.println("You must bet at least one chip.");
            } else if (bet > chips) {
                System.err.println("You do not have that many chips.");
            }
            // If they entered a valid number of chips, break out of while (true) loop
            else {
                break;
            }
        }
    }

    public static void natural() {
        System.out.println("Blackjack!");
        if (dealerTotal + allCards.get(1).value == 21) {
            dealerCards.add(0, allCards.get(1));
            Blackjack.showHands(dealerCards, "Dealer");
            System.out.println("It's a push.");
        } else {
            System.out.println("You win!");
            chips += Math.floor(bet * 1.5);
        }
    }

    public static boolean insurance() {
        boolean dealerBlackjack = false;
        if (dealerCards.get(0).name.equals("Ace") && bet * 1.5 < chips && bet != 1) {
            while (true) {
                System.out.println("Would you like insurance? (Y/N)");
                String wantsInsurance = input.next();

                if (wantsInsurance.equalsIgnoreCase("Y")) {
                    System.out.println("Insurance taken.");
                    if (allCards.get(1).value == 10) {
                        dealerCards.add(0, allCards.get(1));
                        Blackjack.showHands(dealerCards, "Dealer");
                        System.out.println("Dealer has a blackjack.");
                        System.out.println("Insurance paid.");
                        dealerBlackjack = true;
                    }
                    else {
                        System.out.println("Nobody's home.");
                        chips -= bet / 2;
                    }
                    break;
                } else if (wantsInsurance.equalsIgnoreCase("N")) {
                    break;
                } else {
                    System.err.println("Unknown command entered. Please try again.");
                }
            }
        }
        return dealerBlackjack;
    }

    public static int checkIfAce(ArrayList<Card> hand, int total) {
        if (total > 21) {
            for (Card card: hand) {
                if (card.value == 11) {
                    card.value = 1;
                    total -= 10;
                    break;
                }
            }
        }
        return total;
    }

    public static int playerTurn(ArrayList<Card> hand, int total) {
        int turn = 1;
        String play;
        boolean doubleDown = false;

        while (true) {
            if (total == 21) {
                break;
            }

            if (turn == 1 && (bet * 2) <= chips) {
                if (hand.get(0).name.equals(hand.get(1).name)) {
                    System.out.println("Would you like to [H]it, [S]tay, [D]ouble Down, or [Sp]lit?");
                    play = input.next();

                    if (play.equalsIgnoreCase("SP")) {
                        split = true;
                        cardCount = 6;
                        if (playerCards.get(0).value == 1) {
                            playerCards.get(0).value = 11;
                        }
                        playerCards.set(1, allCards.get(4));
                        playerCardsSplit.add(allCards.get(2));
                        playerCardsSplit.add(allCards.get(5));
                        playerTotal = playerCards.get(0).value + playerCards.get(1).value;
                        playerTotalSplit = playerCardsSplit.get(0).value + playerCardsSplit.get(1).value;
                        break;
                    }
                }
                else {
                    System.out.println("Would you like to [H]it, [S]tay, or [D]ouble Down?");
                    play = input.next();
                }
                if (play.equalsIgnoreCase("D")) {
                    doubleDown = true;
                }
            }
            else {
                System.out.println("Would you like to [H]it or [S]tay?");
                play = input.next();
            }

            if (play.equalsIgnoreCase("H") || doubleDown) {
                System.out.println("Your new card is: " + allCards.get(cardCount));
                hand.add(allCards.get(cardCount));
                total += allCards.get(cardCount).value;

                total = Blackjack.checkIfAce(hand, total);

                System.out.println("Your new total is: " + total);
            } else if (play.equalsIgnoreCase("S")) {
                break;
            } else {
                System.err.println("Unknown command entered. Please try again.");
                continue;
            }

            turn++;
            cardCount++;

            if (doubleDown) {
                bet *= 2;
            }

            if (total > 21) {
                System.out.println("You bust.");
                break;
            }

            if (doubleDown) {
                break;
            }
        }
        System.out.println();
        return total;
    }

    public static void dealerTurn() throws InterruptedException {
        boolean dealerHitOn17 = false;

        while (true) {
            Thread.sleep(1000);
            if (dealerTotal == 17) {
                for (Card card: dealerCards) {
                    if (card.value == 11) {
                        dealerHitOn17 = true;
                        break;
                    }
                }
            }

            if (dealerTotal < 17 || dealerHitOn17) {
                System.out.println("Dealer hits.");
                System.out.println("Dealer's new card is: " + allCards.get(cardCount));
                dealerCards.add(allCards.get(cardCount));
                dealerTotal += allCards.get(cardCount).value;
                dealerTotal = Blackjack.checkIfAce(dealerCards, dealerTotal);
                System.out.println("Dealer's new total is: " + dealerTotal);
                System.out.println();
                cardCount++;
                dealerHitOn17 = false;
            }
            else if (dealerTotal <= 21) {
                System.out.println("Dealer stays.");
                break;
            } else {
                System.out.println("Dealer busts.");
                break;
            }
        }

    }

    public static void compareHands(int pTotal) {
        if (pTotal > 21) {
            System.out.println("You lose.");
            chips -= bet;
        } else if (dealerTotal > 21) {
            System.out.println("You win!");
            chips += bet;
        } else if (pTotal == dealerTotal) {
            System.out.println("It's a push.");
        } else if (pTotal > dealerTotal) {
            System.out.println("You win!");
            chips += bet;
        } else {
            System.out.println("You lose.");
            chips -= bet;
        }
    }

    // public static void main
    // Main method
    public static void main(String[] args) throws InterruptedException {
        Blackjack.howManyDecks();
        // While player still has chips
        while (chips > 0) {
            Blackjack.cardSetUp();

            Blackjack.reset();

            Blackjack.bet();

            // If player would like to exit, break out of while (chips > 0) loop
            if (exit) {
                break;
            }

            playerTotal = Blackjack.checkIfAce(playerCards, playerTotal);
            Blackjack.showHands(playerCards, "Player");
            System.out.println("Dealer's cards: Hidden Card, " + dealerCards.get(0));
            System.out.println("Dealer's total: " + dealerTotal);
            System.out.println();

            if (playerTotal == 21) {
                Blackjack.natural();
                continue;
            }

            if (Blackjack.insurance()) {
                continue;
            }

            playerTotal = Blackjack.playerTurn(playerCards, playerTotal);

            if (split) {
                Blackjack.showHands(playerCards, "Player");
                playerTotal = Blackjack.playerTurn(playerCards, playerTotal);
                Blackjack.showHands(playerCardsSplit, "Player");
                playerTotalSplit = Blackjack.playerTurn(playerCardsSplit, playerTotalSplit);
            }

            Thread.sleep(1000);

            dealerCards.add(0, allCards.get(1));
            dealerTotal += dealerCards.get(0).value;

            dealerTotal = Blackjack.checkIfAce(dealerCards, dealerTotal);

            if (!split) {
                Blackjack.showHands(playerCards, "Player");
            } else {
                Blackjack.showHands(playerCards, "Player Hand #1");
                Blackjack.showHands(playerCards, "Player Hand #2");
            }

            Blackjack.showHands(dealerCards, "Dealer");

            // Dealer turn
            Blackjack.dealerTurn();

            Blackjack.compareHands(playerTotal);
            if (split) {
                Blackjack.compareHands(playerTotalSplit);
            }

        } // end: while (chips > 0)

        System.out.println("Thank you for playing!");
    }
    // end: Main method
}
// end: public class Blackjack