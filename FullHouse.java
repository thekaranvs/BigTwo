/**
 * Models a FullHouse hand in Big Two, subclass of Hand
 * @author karanvs
 *
 */
public class FullHouse extends Hand {
	
	/**
	 * Constructor to create a Full House hand
	 * @param player The player who is playing this hand
	 * @param cards The list of cards the player wishes to play
	 */
	public FullHouse(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * a method for retrieving the top card of this hand
	 * @return The top card in this hand
	 */
	public Card getTopCard() {
		if (!this.isEmpty()) {
			this.sort();
			int rank1 = -1, rank2 = -1;
			int counter1 = 0, counter2 = 0;
			
			for (int i = 0; i < this.size(); i++) {
				if (rank1 == -1)
					rank1 = this.getCard(i).rank;
				if (rank1 != -1 && rank1 != this.getCard(i).rank && rank2 == -1)
					rank2 = this.getCard(i).rank;

				if (rank1 == this.getCard(i).rank) {
					counter1++;
				} else if (rank2 == this.getCard(i).rank) {
					counter2++;
				}
			}
			// Assuming hand is valid
			if (counter1 == 3) {
				return this.getCard(2);
			} else {
				return this.getCard(4);
			}
		} else {
			return null;
		}
	}
	
	/**
	 * a method for checking if this Full House hand beats a specified hand
	 * @param hand The hand of cards we are comparing against
	 * @return boolean value specifying if current hand beats the provided hand
	 */
	public boolean beats(Hand hand) {
		if (hand == null || !hand.isValid() || !this.isValid() || hand.size() != this.size()) {
			return false;
		}
		String handType = hand.getType();
		if (handType == "Straight" || handType == "Flush") {
			return true;
		} else if (handType == "Quad" || handType == "StraightFlush") {
			return false;
		}
		// compare rank of their top cards
		return (this.getTopCard().compareTo(hand.getTopCard()) > 0);
	}
	
	/**
	 * a method for checking if this is a valid Full House hand
	 * @return boolean value specifying if Full House hand is valid
	 */
	public boolean isValid() {
		if (this.size() != 5) {
			return false;
		}
		this.sort();

		int rank1 = -1, rank2 = -1;
		int counter1 = 0, counter2 = 0;
		
		for (int i = 0; i < this.size(); i++) {
			if (rank1 == -1)
				rank1 = this.getCard(i).rank;
			if (rank1 != -1 && rank1 != this.getCard(i).rank && rank2 == -1)
				rank2 = this.getCard(i).rank;

			if (rank1 == this.getCard(i).rank) {
				counter1++;
			} else if (rank2 == this.getCard(i).rank) {
				counter2++;
			} else {
				return false;
			}
		}
		
		if ((counter1 == 2 && counter2 == 3) || (counter1 == 3 && counter2 == 2)) {
			return true;
		}
		return false;
	}
	
	/**
	 * a method for returning a string specifying the type of this hand
	 * @return string value specifying the type of this hand
	 */
	public String getType() {
		return "FullHouse";
	}
}
