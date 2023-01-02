/**
 * Models a Triple hand in Big Two, subclass of Hand
 * @author karanvs
 *
 */
public class Triple extends Hand {

	/**
	 * Constructor to create a Triple hand
	 * @param player The player who is playing this hand
	 * @param cards The list of cards the player wishes to play
	 */
	public Triple(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * a method for checking if this is a valid Triple hand
	 * @return boolean value specifying if Triple hand is valid
	 */
	public boolean isValid() {
		return (this.size() == 3 && ((this.getCard(0).rank == this.getCard(1).rank) && (this.getCard(0).rank == this.getCard(2).rank)));
	}
	
	/**
	 * a method for returning a string specifying the type of this hand
	 * @return string value specifying the type of this hand
	 */
	public String getType() {
		return "Triple";
	}
}
