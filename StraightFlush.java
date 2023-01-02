/**
 * Models a Straight Flush hand in Big Two, subclass of Hand
 * @author karanvs
 *
 */
public class StraightFlush extends Hand {
	
	private int[] rankScore = {12, 13, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
	
	/**
	 * Constructor to create a Straight Flush hand
	 * @param player The player who is playing this hand
	 * @param cards The list of cards the player wishes to play
	 */
	public StraightFlush(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * a method for checking if this hand beats a specified hand
	 * @param hand The hand of cards we are comparing against
	 * @return boolean value specifying if current hand beats the provided hand
	 */
	public boolean beats(Hand hand) {
		if (hand == null || !hand.isValid() || !this.isValid() || hand.size() != this.size()) {
			return false;
		}
		String handType = hand.getType();
		if (handType == "Straight" || handType == "Flush" || handType == "FullHouse" || handType == "Quad") {
			return true;
		}
		// compare ranks of their top cards, if ranks are same, compare suit
		return (this.getTopCard().compareTo(hand.getTopCard()) > 0);
	}
	
	/**
	 * a method for checking if this is a valid Quad hand
	 * Assumes hand is legal (i.e. no duplicate cards etc.)
	 * @return boolean value specifying if Quad hand is valid
	 */
	public boolean isValid() {
		if (this.size() != 5) {
			return false;
		}
		this.sort();
		
		int commonSuit = this.getCard(0).suit;
		int[] cardScores = new int[this.size()];
		
		for (int i = 0; i < this.size(); i++) {
			cardScores[i] = rankScore[this.getCard(i).rank];
			if (commonSuit != this.getCard(i).suit) {
				return false;
			}
		}
		
		// Ensure that ranks of card are consecutive
		for (int i = 0; i < cardScores.length - 1; i++) {
			if (!(cardScores[i] == (cardScores[i+1] - 1))) {
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
		return "StraightFlush";
	}
}
