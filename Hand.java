/**
 * Hand class models a hand of cards and is a subclass of CardList
 * Additionally stores the player and provides framework for its subclasses
 * @author karanvs
 *
 */
abstract class Hand extends CardList {
	
	private CardGamePlayer player;
	
	/**
	 * a constructor for building a hand with the specified player and list of cards
	 * @param player The player who played the current hand
	 * @param cards CardList object containing the cards in hand
	 */
	public Hand(CardGamePlayer player, CardList cards) {
		this.player = player;
		for (int i = 0; i < cards.size(); i++) {
			this.addCard(cards.getCard(i));
		}
		this.sort();
	}
	
	/**
	 * a method for retrieving the player of this hand
	 * @return the player who played this hand
	 */
	public CardGamePlayer getPlayer() {
		return this.player;
	}
	
	/**
	 * a method for retrieving the top card of this hand
	 * Returns the last card in a sorted hand by default (works for most subclasses)
	 * @return The top card in this hand
	 */
	public Card getTopCard() {
		if (!this.isEmpty()) {
			this.sort();
			return this.getCard(this.size()-1);
		} else {
			return null;
		}
	}
	
	/**
	 * a method for checking if this hand beats a specified hand
	 * @param hand The hand of cards we are comparing against
	 * @return boolean value specifying if current hand beats the provided hand
	 */
	public boolean beats(Hand hand) {
		if (hand == null || !hand.isValid() || !this.isValid() || this.getType() != hand.getType()) {
			return false;
		} else {
			return (this.getTopCard().compareTo(hand.getTopCard()) > 0);
		}
	}
	
	/**
	 * a method for checking if this is a valid hand
	 * @return boolean value specifying if hand is valid
	 */
	public abstract boolean isValid();
	
	/**
	 * a method for returning a string specifying the type of this hand
	 * @return string value specifying the type of this hand
	 */
	public abstract String getType();

}
