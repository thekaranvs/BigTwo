/**
 * Models a Flush hand in Big Two, subclass of Hand
 * @author karanvs
 *
 */
public class Flush extends Hand {
	
	/**
	 * Constructor to create a Flush hand
	 * @param player The player who is playing this hand
	 * @param cards The list of cards the player wishes to play
	 */
	public Flush(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * a method for checking if this Flush hand beats a specified hand
	 * @param hand The hand of cards we are comparing against
	 * @return boolean value specifying if current hand beats the provided hand
	 */
	public boolean beats(Hand hand) {
		if (hand == null || !hand.isValid() || !this.isValid() || hand.size() != this.size()) {
			return false;
		}
		String handType = hand.getType();
		if (handType == "Straight") {
			return true;
		} else if (handType == "FullHouse" || handType == "Quad" || handType == "StraightFlush") {
			return false;
		}
		// compare the suit
		if (this.getTopCard().suit > hand.getTopCard().suit) {
			return true;
		} else if (this.getTopCard().suit < hand.getTopCard().suit) {
			return false;
		}
		// if hands have same suit, compare rank of their top cards
		return (this.getTopCard().compareTo(hand.getTopCard()) > 0);
	}
	
	/**
	 * a method for checking if this is a valid Flush hand
	 * @return boolean value specifying if Flush hand is valid
	 */
	public boolean isValid() {
		if (this.size() != 5) {
			return false;
		}
		int commonSuit = this.getCard(0).suit;
		for (int i = 1; i < this.size(); i++) {
			if (commonSuit != this.getCard(i).suit) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * a method for returning a string specifying the type of this hand
	 * @return string value specifying the type of this hand
	 */
	public String getType() {
		return "Flush";
	}
}
