/**
 * The BigTwoDeck class is a subclass of the Deck class and is used to model a deck of cards used in a Big Two card game
 * @author karanvs
 *
 */
public class BigTwoDeck extends Deck {

	/**
	 * Initializes the deck of cards (instances of BigTwoCard) (called implicitly inside the constructor).
	 */
	public void initialize() {
		removeAllCards();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 13; j++) {
				BigTwoCard card = new BigTwoCard(i, j);
				addCard(card);
			}
		}
	}
}
