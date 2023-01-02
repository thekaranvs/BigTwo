/**
 * Models a Pair hand in Big Two, subclass of Hand
 * @author karanvs
 *
 */
public class Pair extends Hand {

	/**
	 * Constructor to create a Pair hand
	 * @param player The player who is playing this hand
	 * @param cards The list of cards the player wishes to play
	 */
	public Pair(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * a method for checking if this is a valid pair hand
	 * @return boolean value specifying if pair hand is valid
	 */
	public boolean isValid() {
		return (this.size() == 2 && (this.getCard(0).rank == this.getCard(1).rank));
	}
	
	/**
	 * a method for returning a string specifying the type of this hand
	 * @return string value specifying the type of this hand
	 */
	public String getType() {
		return "Pair";
	}
}
