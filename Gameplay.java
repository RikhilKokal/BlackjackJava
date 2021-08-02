import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Gameplay {
    Scanner input = new Scanner(System.in);
    boolean exit = false;
    int numOfDecks = 1;
    int chips = 1000;
    int bet = 0;
    int cardCount = 4;
    boolean split = false;
    ArrayList<Card> allCards = new ArrayList<>();
    ArrayList<Card> playerCards = new ArrayList<>();
    ArrayList<Card> playerCardsSplit = new ArrayList<>();
    ArrayList<Card> dealerCards = new ArrayList<>();
    int playerTotal = 0;
    int dealerTotal = 0;
    int playerTotalSplit = 0;
    HashMap<String, Integer> allScores = new HashMap<>();
    File scoresFile = new File("BlackjackScores.txt");
    boolean loadUsername = false;
    String username;

    public void loadSave() {
        try {
            if (scoresFile.exists()) {
                Scanner fileReader = new Scanner(scoresFile);
                while (fileReader.hasNextLine()) {
                    String currentScore = fileReader.nextLine();
                    byte[] decodedData = Base64.getDecoder().decode(currentScore);
                    String[] currentScoreSplit = new String(decodedData).split(":");

                    allScores.put(currentScoreSplit[0], Integer.parseInt(currentScoreSplit[1]));
                }
                if (allScores.size() != 0) {
                    while (true) {
                        System.out.println("Would you like to load a save file? (Y/N)");
                        String load = input.next();
                        if (load.equalsIgnoreCase("Y")) {
                            loadUsername = true;
                            break;
                        } else if (load.equalsIgnoreCase("N")) {
                            break;
                        } else {
                            System.err.println("Unknown command entered. Please try again.");
                        }
                    }
                }
            } else {
                scoresFile.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("An error occurred. Please try again.");
            e.printStackTrace();
        }
        if (loadUsername) {
            while (true) {
                System.out.println("What is your username?");
                username = input.next();
                if (allScores.containsKey(username)) {
                    chips = allScores.get(username);
                    break;
                } else {
                    System.err.println("That username has not been saved.");
                }
            }
        }
    }

    public void howManyDecks() {
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

    public void cardSetUp() {
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
                allCards.add(newCard); // Adds newly-created card to allCards ArrayList
            }
        }
    }

    public void reset() {
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

    public void showHands(ArrayList<Card> hand, String playerOrDealer) {
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

    public void bet() {
        while (true) {
            System.out.println("You have " + chips + " chips. Please enter the number of chips you want to bet or [E]xit.");
            String betOrExit = input.next(); // Ask for input
            // If player would like to exit
            if (betOrExit.equalsIgnoreCase("E")) {
                exit = true;
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
                System.out.println();
                break;
            }
        }
    }

    public void natural() {
        System.out.println("Blackjack!");
        if (dealerTotal + allCards.get(1).value == 21) {
            dealerCards.add(0, allCards.get(1));
            this.showHands(dealerCards, "Dealer");
            System.out.println("It's a push.");
        } else {
            System.out.println("You win!");
            chips += Math.floor(bet * 1.5);
        }
    }

    public boolean insurance() {
        boolean dealerBlackjack = false;
        if (dealerCards.get(0).name.equals("Ace") && bet * 1.5 < chips && bet != 1) {
            while (true) {
                System.out.println("Would you like insurance? (Y/N)");
                String wantsInsurance = input.next();

                if (wantsInsurance.equalsIgnoreCase("Y")) {
                    System.out.println("Insurance taken.");
                    if (allCards.get(1).value == 10) {
                        dealerCards.add(0, allCards.get(1));
                        this.showHands(dealerCards, "Dealer");
                        System.out.println("Dealer has a blackjack.");
                        System.out.println("Insurance paid.");
                        dealerBlackjack = true;
                    } else {
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

    public int checkIfAce(ArrayList<Card> hand, int total) {
        if (total > 21) {
            for (Card card : hand) {
                if (card.value == 11) {
                    card.value = 1;
                    total -= 10;
                    break;
                }
            }
        }
        return total;
    }

    public int playerTurn(ArrayList<Card> hand, int total) {
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
                } else {
                    System.out.println("Would you like to [H]it, [S]tay, or [D]ouble Down?");
                    play = input.next();
                }
                if (play.equalsIgnoreCase("D")) {
                    doubleDown = true;
                }
            } else {
                System.out.println("Would you like to [H]it or [S]tay?");
                play = input.next();
            }

            if (play.equalsIgnoreCase("H") || doubleDown) {
                System.out.println("\nYour new card is: " + allCards.get(cardCount));
                hand.add(allCards.get(cardCount));
                total += allCards.get(cardCount).value;

                total = this.checkIfAce(hand, total);

                System.out.println("Your new total is: " + total + "\n");
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

    public void dealerTurn() throws InterruptedException {
        boolean dealerHitOn17 = false;

        while (true) {
            Thread.sleep(1000);
            if (dealerTotal == 17) {
                for (Card card : dealerCards) {
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
                dealerTotal = this.checkIfAce(dealerCards, dealerTotal);
                System.out.println("Dealer's new total is: " + dealerTotal);
                System.out.println();
                cardCount++;
                dealerHitOn17 = false;
            } else if (dealerTotal <= 21) {
                System.out.println("Dealer stays.");
                break;
            } else {
                System.out.println("Dealer busts.");
                break;
            }
        }

    }

    public void compareHands(int pTotal) {
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
        System.out.println();
    }

    public void saveScore() throws IOException {
        String save = "";
        String enteredUsername;
        if (!loadUsername) {
            while (true) {
                System.out.println("Would you like to save your score? (Y/N)");
                save = input.next();
                if (save.equalsIgnoreCase("Y") || save.equalsIgnoreCase("N")) {
                    break;
                } else {
                    System.err.println("Unknown command entered. Please try again.");
                }
            }
        }

        if (save.equalsIgnoreCase("Y") || loadUsername) {
            scoresFile.delete();
            File scoresFile = new File("BlackjackScores.txt");
            scoresFile.createNewFile();

            if (save.equalsIgnoreCase("Y")) {
                while (true) {
                    System.out.println("What would you like your username to be? NOTE: You will need to rememmber this username in order to load this save file.");
                    enteredUsername = input.next();

                    if (enteredUsername.length() < 2) {
                        System.err.println("Your username must be at least two characters long.");
                    } else if (allScores.containsKey(enteredUsername)) {
                        System.err.println("That username is already taken.");
                    } else {
                        break;
                    }
                }
            } else {
                enteredUsername = username;
            }

            allScores.put(enteredUsername, chips);

            FileWriter write = new FileWriter(scoresFile);
            for (Map.Entry<String, Integer> key : allScores.entrySet()) {
                String encodedData = Base64.getEncoder().encodeToString((key.getKey() + ":" + key.getValue()).getBytes());
                write.write(encodedData + "\n");
            }
            write.close();
        }
    }
}