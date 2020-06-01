import java.util.Scanner;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PokerHands {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        Pattern p = Pattern.compile("^(?:[0-9TJQKA]|1[0-3])[CDHS](?:([/ -])(?:[0-9TJQKA]|1[1-3])[CDHS]){4}$");

        while (s.hasNextLine()) {
            String feed = s.nextLine();
            String line = feed.toUpperCase();
            Matcher m = p.matcher(line);

            if (!m.find()) {
                System.out.println("Invalid: " + feed);
                continue;
            }

            String delim = m.group(1);
            String[] cardFeed = line.split(delim);

            if (cardFeed.length != 5) {
                System.out.println("Invalid: " + feed);
                continue;
            }

            boolean invalid = false;

            for (int i = 0; i < cardFeed.length - 1; i++) {
                for (int j = i + 1; j < cardFeed.length; j++) {
                    if (cardFeed[i].equals(cardFeed[j])) {
                        invalid = true;
                        break;
                    }
                }

                if (invalid) {
                    break;
                }
            }

            if (invalid) {
                System.out.println("Invalid: " + feed);
                continue;
            }

            Card[] cards = new Card[cardFeed.length];

            for (int i = 0; i < cardFeed.length; i++) {
                int offset = cardFeed[i].length() > 2 ? 1 : 0;
                String val = cardFeed[i].substring(0, 1 + offset);
                String suit = cardFeed[i].substring(1 + offset, 2 + offset);
                cards[i] = new Card(val, suit);
            }

            Comparator<Card> comp = Comparator.comparingInt(Card::getVal).thenComparingInt(Card::getSuit);
            Arrays.sort(cards, comp);

            int i = 0;
            System.out.print(cards[i++]);
            for (; i < cards.length; i++) {
                System.out.print(" " + cards[i]);
            }
            System.out.println();
        }
    }

    private static class Card {
        private int val;
        private int suit;

        public Card(String val, String suit) {
            switch (val) {
                case "T":
                    this.val = 10;
                    break;
                case "J":
                    this.val = 11;
                    break;
                case "Q":
                    this.val = 12;
                    break;
                case "K":
                    this.val = 13;
                    break;
                case "A":
                case "1":
                    this.val = 14;
                    break;
                default:
                    this.val = Integer.parseInt(val);
            }

            switch (suit) {
                case "C":
                    this.suit = 1;
                    break;
                case "D":
                    this.suit = 2;
                    break;
                case "H":
                    this.suit = 3;
                    break;
                case "S":
                    this.suit = 4;
                    break;
            }
        }

        public String toString() {
            String out;

            switch (val) {
                case 11:
                    out = "J";
                    break;
                case 12:
                    out = "Q";
                    break;
                case 13:
                    out = "K";
                    break;
                case 14:
                    out = "A";
                    break;
                default:
                    out = Integer.toString(val);
            }

            switch (suit) {
                case 1:
                    out += "C";
                    break;
                case 2:
                    out += "D";
                    break;
                case 3:
                    out += "H";
                    break;
                case 4:
                    out += "S";
                    break;
            }

            return out;
        }

        public int getVal() {
            return val;
        }

        public int getSuit() {
            return suit;
        }
    }
}
