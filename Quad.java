/**
 * Models a Quad hand in Big Two, subclass of Hand
 * @author karanvs
 *
 */
public class Quad extends Hand {
	
	/**
	 * Constructor to create a Quad hand
	 * @param player The player who is playing this hand
	 * @param cards The list of cards the player wishes to play
	 */
	public Quad(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * a method for retrieving the top card of this hand
	 * @return The top card in this hand
	 */
	public Card getTopCard() {
		if (!this.isEmpty()) {
			this.sort();
			// Assuming it is a valid quad, the 4 cards with same rank are either in first 4
			// spots or in the last 4 spots - the one with highest suit is 4th card in case 1 and last card in 2nd case
			if (this.getCard(0).rank == this.getCard(1).rank) {
				return this.getCard(3);
			} else {
				return this.getCard(4);
			}
		} else {
			return null;
		}
	}
	
	/**
	 * a method for checking if this Quad hand beats a specified hand
	 * @param hand The hand of cards we are comparing against
	 * @return boolean value specifying if current hand beats the provided hand
	 */
	public boolean beats(Hand hand) {
		if (hand == null || !hand.isValid() || !this.isValid() || hand.size() != this.size()) {
			return false;
		}
		String handType = hand.getType();
		if (handType == "Straight" || handType == "Flush" || handType == "FullHouse") {
			return true;
		} else if (handType == "StraightFlush") {
			return false;
		}
		// compare ranks of their top cards
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
		
		int numOfRanks = 0;
		boolean fourWithSameRank = false;
		for (int i = 0; i < this.size(); i++) {
			int originali = i;
			while (i < this.size()-1 && this.getCard(i).rank == this.getCard(i+1).rank) {
				i++;
			}
			int numOfCardsWithSameRank = i - originali + 1;
			if (numOfCardsWithSameRank == 4) {
				fourWithSameRank = true;
			}
			numOfRanks++;
		}
		if (numOfRanks == 2 && fourWithSameRank) {
			return true;
		}
		return false;
	}
	
	/**
	 * a method for returning a string specifying the type of this hand
	 * @return string value specifying the type of this hand
	 */
	public String getType() {
		return "Quad";
	}
}
