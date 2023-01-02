/**
 * The BigTwoCard class is a subclass of the Card class and is used to model a card used in a Big Two card game
 * @author karanvs
 *
 */
public class BigTwoCard extends Card {
	
	// 'score' assigned to cards: A 2 3 4 5 6 7 8 9 10 J Q K
	private int[] rankScore = {12, 13, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
	// can use formula (rank + 11) % 13 to reach same scoring system
	
	/**
	 * a constructor for building a card with the specified suit and rank
	 * @param suit The suit of the Big Two Card
	 * @param rank The rank of the Big Two Card
	 */
	public BigTwoCard(int suit, int rank) {
		super(suit, rank);
	}
	
	/**
	 * Compares this card with the specified card for order (as per Big Two's rules)
	 * 
	 * @param card the card to be compared
	 * @return a negative integer, zero, or a positive integer as this card is less
	 *         than, equal to, or greater than the specified card (as per Big Two's rules)
	 */
	public int compareTo(Card card) {
		if (rankScore[this.rank] > rankScore[card.rank]) {
			return 1;
		} else if (rankScore[this.rank] < rankScore[card.rank]) {
			return -1;
		} else if (this.suit > card.suit) {
			return 1;
		} else if (this.suit < card.suit) {
			return -1;
		} else {
			return 0;
		}
	}
}
