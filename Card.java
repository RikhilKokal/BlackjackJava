// class Card
// Creates and manages cards
//
public class Card {
    String name; // Card name
    String suits; // Card suit
    int value; // Card numerical Value

    // Constructor for Card class
    //
    // Arguments --
    // name: Card name
    // suits: Card suit
    // value: Card numerical value
    //
    public Card(String name, String suits) {
        this.name = name; // Card name
        this.suits = suits; // Card suits
        this.value = switch (this.name) {
            case "Ace" -> 11;
            case "Jack", "Queen", "King" -> 10;
            default -> Integer.parseInt(this.name);
        };
    }
    // end: public Card

    // public String toString
    //
    // Java toString method
    //
    public String toString() {
        return name + suits;
    }
    // end: public String toString
}
// end: class Card