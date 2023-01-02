import java.util.ArrayList;

/**
 * The BigTwo class implements the CardGame interface and is used to model a Big Two card game
 * @author karanvs
 *
 */
public class BigTwo implements CardGame {
	
	private int numOfPlayers;
	private Deck deck;
	private ArrayList<CardGamePlayer> playerList;
	private ArrayList<Hand> handsOnTable;
	private int currentPlayerIdx;
	private BigTwoGUI gui;
	private BigTwoClient client;
	
	/**
	 * Boolean array to track which players are connected to server to help with gui printing
	 */
	public boolean[] playerPresent = {false, false, false, false};
	
	/**
	 * Constructor for creating a Big Two card game
	 * Creates 4 players, BigTwoUI (and links it) and initializes the other instance variables
	 */
	public BigTwo() {
		
		numOfPlayers = 4;
		currentPlayerIdx = -1;
		playerList = new ArrayList<CardGamePlayer>();
		handsOnTable = new ArrayList<Hand>();
		for (int i=0; i<numOfPlayers; i++) {
			playerList.add(new CardGamePlayer());
		}
		
		gui = new BigTwoGUI(this);
		client = new BigTwoClient(this, gui);
	}
	
	/**
	 * Function to return player name set in BigTwoClient
	 * @return string of player's name
	 */
	public String getPlayerName() {
		return client.getPlayerName();
	}
	
	/**
	 * Function to return player ID
	 * @return integer value specifying player ID
	 */
	public int getPlayerID() {
		return client.getPlayerID();
	}
	
	/**
	 * Function to call BigTwoClient's send message to server
	 * @param message CardGameMessage to be sent
	 */
	public synchronized void sendMessage(CardGameMessage message) {
		client.sendMessage(message);
	}
	
	/**
	 * Function to attempt to connect with BigTwoGame Server
	 */
	public void connect() {
		client.connect();
	}
	
	/**
	 * Calls disconnect function of BigTwoClient
	 */
	public void disconnect() {
		client.disconnect();
	}
	
	/**
	 * Returns the number of players in this card game.
	 * 
	 * @return the number of players in this card game
	 */
	public int getNumOfPlayers() {
		return this.numOfPlayers;
	}
	
	/**
	 * Returns the deck of cards being used in this card game.
	 * 
	 * @return the deck of cards being used in this card game
	 */
	public Deck getDeck() {
		return this.deck;
	}
	
	/**
	 * Returns the list of players in this card game.
	 * 
	 * @return the list of players in this card game
	 */
	public ArrayList<CardGamePlayer> getPlayerList() {
		return this.playerList;
	}
	
	/**
	 * Returns the list of hands played on the table.
	 * 
	 * @return the list of hands played on the table
	 */
	public ArrayList<Hand> getHandsOnTable() {
		return this.handsOnTable;
	}
	
	/**
	 * Returns the index of the current player.
	 * 
	 * @return the index of the current player
	 */
	public int getCurrentPlayerIdx() {
		return this.currentPlayerIdx;
	}
	
	/**
	 * Starts the card game.
	 * 
	 * @param deck the deck of (shuffled) cards to be used in this game
	 */
	public void start(Deck deck) {
		// Remove all cards from all players
		for (CardGamePlayer player : this.playerList) {
			player.removeAllCards();
		}
		
		// Remove cards from table and reset currentPlayerIdx to -1
		this.handsOnTable.clear();
		this.currentPlayerIdx = -1;
		
		// temporary variable which stores the three of diamonds card to help identify it in deck
		Card threeDiamond = new Card(0, 2);
		// not sure about function of deck - currently deciding to store cards in deck and removing them from deck as they're played on table
		this.deck = deck; 
		
		// Distribute cards to players
		for (int i=0; i<deck.size(); i++) {
			// Distributes the shuffled cards in deck to the players in sequence
			this.playerList.get(i%4).addCard(deck.getCard(i));
			
			// If card is three of diamonds, identify the player who holds it
			if (deck.getCard(i).equals(threeDiamond)) {
				this.currentPlayerIdx = (i%4);
			}
		}
		
		// sort cards in players hands once for ease of playing game
		for (CardGamePlayer player : this.playerList) {
			player.sortCardsInHand();
		}
		
		// Set the active player in BigTwoGUI, call repaint and prompt the player
		this.promptPlayer();
	}
	
	// internal private function to set active player, repaint the gui and prompt the active player
	private void promptPlayer() {
		gui.setActivePlayer(this.currentPlayerIdx);
		if (client.getPlayerID() == this.currentPlayerIdx)
			gui.enableTable();
		else
			gui.disableTable();
		gui.repaint();
		gui.promptActivePlayer();
	}
	
	/**
	 * Makes a move by the player.
	 * 
	 * @param playerIdx the index of the player who makes the move
	 * @param cardIdx   the list of the indices of the cards selected by the player
	 */
	public void makeMove(int playerIdx, int[] cardIdx) {
		CardGameMessage move = new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx);
		sendMessage(move);
	}
	
	/**
	 * Checks the move made by the player.
	 * 
	 * @param playerIdx the index of the player who makes the move
	 * @param cardIdx   the list of the indices of the cards selected by the player
	 */
	public void checkMove(int playerIdx, int[] cardIdx) {
		CardGamePlayer player = this.playerList.get(playerIdx);
		CardList selectedCards = player.play(cardIdx);
		
		if(client.getPlayerID() == this.currentPlayerIdx) gui.enableTable();
		else gui.disableTable();
		
		// need to check if user can pass
		if (selectedCards == null) {
			CardGamePlayer lastPlayer = (handsOnTable.size() == 0) ? null : this.handsOnTable.get(handsOnTable.size() - 1).getPlayer();
			
			if (lastPlayer == null || lastPlayer.equals(player)) {
				notifyInvalidMove();
				return;
			} else {
				this.currentPlayerIdx = (this.currentPlayerIdx + 1) % 4;
				gui.printMsg("{Pass}\n");
				// after passing, prompt the next player
				this.promptPlayer();
				return;
			}
		}
		
		Hand playingHand = composeHand(player, selectedCards);
		
		if (playingHand == null) {
			// if a valid hand cannot be composed, inform user and prompt again
			notifyInvalidMove();
			return;
		}
		
		// first move needs to include 3 of diamonds
		if (handsOnTable.isEmpty()) {
			if (!selectedCards.contains(new Card(0, 2))) {
				notifyInvalidMove();
				return;
			}
		}
		
		Hand lastPlayed = (handsOnTable.size() == 0) ? null : this.handsOnTable.get(handsOnTable.size() - 1);
		
		if (lastPlayed == null || lastPlayed.getPlayer().equals(player) || playingHand.beats(lastPlayed)) {
			playValidHand(playerIdx, playingHand);
			return;
		} else {
			notifyInvalidMove();
			return;
		}
		
	}
	
	private void notifyInvalidMove() {
		gui.printMsg("Not a legal move!!!\n");
		gui.promptActivePlayer();
	}
	
	/**
	 * Plays the hand chosen by player - adds it to hands on table, updates variables and prints appropriate messages
	 * @param playerIdx The index of the active player 
	 * @param playingHand The hand the player wishes to play
	 */
	private void playValidHand(int playerIdx, Hand playingHand) {
		
		CardGamePlayer player = this.playerList.get(playerIdx);
		player.removeCards(playingHand);
		player.sortCardsInHand();
		
		for(int i = 0; i < playingHand.size(); i++) {
			deck.removeCard(playingHand.getCard(i));
		}
		
		handsOnTable.add(playingHand);
		
		gui.printMsg(String.format("{%s} ", playingHand.getType()));
		if (playingHand.size() > 0) {
			for (int i = 0; i < playingHand.size(); i++) {
				String string = "";
				string = string + "[" + playingHand.getCard(i) + "]";
				if (i % 13 != 0) {
					string = " " + string;
				}
				gui.printMsg(string);
				if (i % 13 == 12 || i == playingHand.size() - 1) {
					gui.printMsg("");
				}
			}
		}
		gui.printMsg("\n");
		
		this.currentPlayerIdx = (this.currentPlayerIdx + 1) % 4;
		gui.repaint();
		if (endOfGame())
			showResults();
		else
			promptPlayer();
	}
	
	/**
	 * Checks for end of game.
	 * 
	 * @return true if the game ends; false otherwise
	 */
	public boolean endOfGame() {
		for (CardGamePlayer player : playerList) {
			if (player.getNumOfCards() == 0) {
				return true;
			}
		}
		return false;
	}
	
	private void showResults() {
		gui.clearMsgArea();
		String gameOverText = "";
		
		this.currentPlayerIdx = -1;
		gui.setActivePlayer(currentPlayerIdx);
		gui.disable();
		gui.repaint();
		gameOverText += "\nGame ends\n";
		for (int i = 0; i < playerList.size(); i++) {
			if (playerList.get(i).getNumOfCards() == 0) {
				gameOverText += this.playerList.get(i).getName() + " wins the game.\n";
			}
			else {
				gameOverText += this.playerList.get(i).getName() + " has " + playerList.get(i).getNumOfCards() + " cards in hand.\n";
			}
		}
		gui.printMsg(gameOverText+"\n");
		gui.displayDialog(gameOverText);
		sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
	}
	
	/**
	 * a method for starting a Big Two card game
	 * @param args command line arguments passed to program
	 */
	public static void main(String[] args) {
		BigTwo game = new BigTwo();
	}
	
	/**
	 * a method for returning a valid hand from the specified list of cards of the player
	 * @param player The player the cards belong to
	 * @param cards The list of cards the player wishes to play
	 * @return A valid sub type of Hand with specified cards or null if no valid hand can be formed
	 */
	public static Hand composeHand(CardGamePlayer player, CardList cards) {
		Hand validHand = null;
		switch (cards.size()) {
			case 1:
				validHand = new Single(player, cards);
				if (validHand.isValid()) {
					break;
				}
				validHand = null;
				break;
			case 2:
				validHand = new Pair(player, cards);
				if (validHand.isValid()) {
					break;
				}
				validHand = null;
				break;
			case 3:
				validHand = new Triple(player, cards);
				if (validHand.isValid()) {
					break;
				}
				validHand = null;
				break;
			case 5:
				validHand = new StraightFlush(player, cards);
				if (validHand.isValid()) {
					break;
				}
				validHand = new Quad(player, cards);
				if (validHand.isValid()) {
					break;
				}
				validHand = new FullHouse(player, cards);
				if (validHand.isValid()) {
					break;
				}
				validHand = new Flush(player, cards);
				if (validHand.isValid()) {
					break;
				}
				validHand = new Straight(player, cards);
				if (validHand.isValid()) {
					break;
				}
				validHand = null;
				break;
			default:
				validHand = null;
				break;
				
		}
		return validHand;
	}
}
