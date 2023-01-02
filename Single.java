/**
 * Models a hand of Single in Big Two, subclass of Hand
 * @author karanvs
 *
 */
public class Single extends Hand {

	/**
	 * Constructor to initialize Single Hand with player and card
	 * @param player The player who is playing this hand
	 * @param cards The list containing the card the player wants to play
	 */
	public Single(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * a method for checking if this is a valid single hand
	 * @return boolean value specifying if single hand is valid
	 */
	public boolean isValid() {
		return (this.size() == 1);
	}
	
	/**
	 * a method for returning a string specifying the type of this hand
	 * @return string value specifying the type of this hand
	 */
	public String getType() {
		return "Single";
	}
}
