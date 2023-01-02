import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

/**
 * BigTwoGUI used to build a GUI for the Big Two card game and handle all user actions
 * Implements the CardGameUI interface
 * @author karanvs
 *
 */
public class BigTwoGUI implements CardGameUI {
	
	private final static int MAX_CARD_NUM = 13;
	private BigTwo game = null;
	private boolean[] selected;
	private int activePlayer = -1;
	
	// GUI elements
	private JFrame frame;
	private JPanel bigTwoPanel;
	private JButton playButton;
	private JButton passButton;
	private JTextArea msgArea;
	private JTextArea chatArea;
	private JTextField chatInput;
	
	// used for disabling mouse clicks on bigTwoPanel
	private boolean bigTwoPanelEnabled = true;
	
	// Images
	private Image[][] cardImages = new Image[4][13];
	private Image backCardImage;
	private Image[] avatars = new Image[4];
	
	/**
	 * Creates an instance of BigTwoGUI
	 * Imports images, sets active player, and builds a GUI
	 * @param game BigTwo game instance required for GUI to display data
	 */
	public BigTwoGUI(BigTwo game) {
		this.game = game;
		
		this.importImages();
		
		this.activePlayer = game.getCurrentPlayerIdx();
		this.selected = new boolean[MAX_CARD_NUM];
		
		this.buildGUI();	
	}
	
	/**
	 * Function to initialize the Image instance variables of BigTwoGUI
	 * Kept in separate function for cleaner code
	 */
	private void importImages() {
		char[] suitsNaming = {'d', 'c', 'h', 's'};
		char[] ranksNaming = {'a', '2', '3', '4', '5', '6', '7', '8', '9', 't', 'j', 'q', 'k'};
		
		for (int i=0; i<cardImages.length; i++) {
			for (int j=0; j<cardImages[i].length; j++) {
				String imagePath = "cards/" + ranksNaming[j] + suitsNaming[i] + ".gif";
				cardImages[i][j] = new ImageIcon(imagePath).getImage();
			}
		}
		
		backCardImage = new ImageIcon("cards/b.gif").getImage();
		
		for (int i=0; i<4; i++) {
			String imagePath = "avatars/avatar" + i + ".jpg";
			avatars[i] = new ImageIcon(imagePath).getImage();
		}
	}
	
	/**
	 * Build GUI for BigTwo
	 */
	private void buildGUI() {
		// frame
		frame = new JFrame("Big Two");
		frame.setLayout(new GridLayout(1, 2));
		frame.setSize(new Dimension(1024, 768));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Menu Items
		JMenuBar menuBar = new JMenuBar();
		
		// Game Menu
		JMenu gameMenu = new JMenu("Game");
		JMenuItem connectMenuItem = new JMenuItem("Connect");
		JMenuItem quitMenuItem = new JMenuItem("Quit");
		// Register with listeners
		quitMenuItem.addActionListener(new QuitMenuItemListener());
		connectMenuItem.addActionListener(new ConnectMenuItemListener());
		// add to Game Menu
		gameMenu.add(connectMenuItem);
		gameMenu.add(quitMenuItem);
		
		// Message Menu
		JMenu messageMenu = new JMenu("Message");
		JMenuItem clearChatMsgMenuItem = new JMenuItem("Clear Chat Messages");
		clearChatMsgMenuItem.addActionListener(new ClearChatMsgMenuItemListener());
		messageMenu.add(clearChatMsgMenuItem);
		
		menuBar.add(gameMenu);
		menuBar.add(messageMenu);
		
		// add menu bar to frame
		frame.setJMenuBar(menuBar);
		
		
		// BigTwoPanel and buttons setup
		JPanel bigTwoAndButtonsPanel = new JPanel(new BorderLayout());
		bigTwoPanel = new BigTwoPanel();
		bigTwoAndButtonsPanel.add(bigTwoPanel);
		
		JPanel playPassButtonsPanel = new JPanel();
		
		playButton = new JButton("Play");
		playButton.addActionListener(new PlayButtonListener());
		playButton.setMargin(new Insets(2, 5, 2, 5));
		
		passButton = new JButton("Pass");
		passButton.addActionListener(new PassButtonListener());
		passButton.setMargin(new Insets(2, 5, 2, 5));
		
		playPassButtonsPanel.add(playButton);
		playPassButtonsPanel.add(passButton);
		
		bigTwoAndButtonsPanel.add(playPassButtonsPanel, BorderLayout.SOUTH);
		frame.add(bigTwoAndButtonsPanel);
		
		// Message + Chat Area GUI
		JPanel communicationsRHSPanel = new JPanel(new BorderLayout());
		
		// Only text areas panel first
		JPanel msgAndChatPanel = new JPanel();
		msgAndChatPanel.setLayout(new GridLayout(2, 1));
		
		msgArea = new JTextArea();
		msgArea.setEditable(false);
		JScrollPane msgAreaScroll = new JScrollPane (msgArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		DefaultCaret msgAreaCaret = (DefaultCaret) msgArea.getCaret();
		msgAreaCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		msgAndChatPanel.add(msgAreaScroll);
		
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		JScrollPane chatAreaScroll = new JScrollPane (chatArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		DefaultCaret chatAreaCaret = (DefaultCaret) chatArea.getCaret();
		chatAreaCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		msgAndChatPanel.add(chatAreaScroll);
		
		// add text areas to communications panel
		communicationsRHSPanel.add(msgAndChatPanel);
		
		// Chat Input Panel
		JPanel chatInputPanel = new JPanel();
		chatInputPanel.setLayout(new BorderLayout());
		
		JLabel msgLabel = new JLabel("Message: ");
		chatInput = new JTextField();
		chatInput.addActionListener(new chatInputHandler());
		
		chatInputPanel.add(msgLabel, BorderLayout.WEST);
		chatInputPanel.add(chatInput);
		
		communicationsRHSPanel.add(chatInputPanel, BorderLayout.SOUTH);
		
		frame.add(communicationsRHSPanel);
		
		frame.setVisible(true);
	}
	
	/**
	 * Calculates the number of presents connected to game
	 * @return number of players present in game
	 */
	private int numPlayersPresent() {
		int count = 0;
		for (int i=0; i<4; i++) {
			if (game.playerPresent[i]) count++;
		}
		return count;
	}
	
	/**
	 * BigTwoPanel used to build GUI for handling game functionalities (selecting cards, playing, passing etc.)
	 * @author karanvs
	 *
	 */
	private class BigTwoPanel extends JPanel implements MouseListener {
		
		// private instance variables to help draw output to screen
		private int nameStartX = 10;
		private int nameStartY = 20;
		
		private int avatarHeight = 100;
		private int avatarWidth = 90;
		private int cardWidth = 73;
		private int cardHeight = 97;
		
		private int cardStartX = nameStartX + (avatarWidth + 10);
		private int cardOverlappingX = 20;
		private int cardStartY = (nameStartY + 10);
		private int cardRaiseY = -10;
		
		private int spacingY = (avatarHeight + 30);
		
		private int handStartY = cardStartY + (spacingY * game.getNumOfPlayers());
		
		/**
		 * Constructor which sets background of BigTwoPanel and adds the mouse listener
		 */
		BigTwoPanel() {
			this.setBackground(new Color(69, 69, 169));
			this.addMouseListener(this);
		}
		
		/**
		 * Used to render out the big two game table to screen (prints player names, avatars, cards in hand and hands on table)
		 * Provides 2 buttons to play and pass
		 * @param g Graphics object
		 */
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			// Draws the player names, their avatar and their cards
			for (int i=0; i<game.getNumOfPlayers(); i++) {
				
				g.setColor(Color.white);
				g.drawLine(0, cardStartY + (spacingY * i) + avatarHeight + 5, this.getWidth(), cardStartY + (spacingY * i) + avatarHeight + 5);
				
				if (!game.playerPresent[i])
					continue;
				if (i == activePlayer)
					g.setColor(Color.pink);
				else
					g.setColor(Color.white);
				
				String playerIdentifier = "";
				if (i == game.getPlayerID()) {
					playerIdentifier = " (You)";
				}
				
				// print player's name and their avatar
				CardGamePlayer player = game.getPlayerList().get(i);
				g.drawString(player.getName() + playerIdentifier, nameStartX, nameStartY + spacingY * i);
				g.drawImage(avatars[i], nameStartX, 5 + nameStartY + spacingY * i, this);
				
				if (numPlayersPresent() != 4)
					continue;
				
				// print cards of player
				for (int j=0; j<player.getNumOfCards(); j++) {
					Card card = player.getCardsInHand().getCard(j);
					if (i == game.getPlayerID() || activePlayer == -1) {
						int raiseBy = 0;
						if (selected[j])
							raiseBy = cardRaiseY;
						g.drawImage(cardImages[card.suit][card.rank], cardStartX + (cardOverlappingX * j), cardStartY + raiseBy + spacingY * i, this);
					}
					else
						g.drawImage(backCardImage, cardStartX + (cardOverlappingX * j), cardStartY + spacingY * i, this);
				}
				if (player.getNumOfCards() == 0) {
					g.setColor(new Color(69, 240, 120));
					g.drawString(player.getName() + " is the winner!", cardStartX, cardStartY + spacingY*i + (cardHeight/2));
				}
			}
			
			// Hands on table
			ArrayList<Hand> handsOnTable = game.getHandsOnTable();
			if (handsOnTable.isEmpty()) {
				g.setColor(Color.cyan);
				g.drawString("No cards on table", nameStartX, handStartY);
			} else {
				Hand lastHand = handsOnTable.get(handsOnTable.size() - 1);
				g.drawString("Played by " + lastHand.getPlayer().getName(), nameStartX, handStartY-10);
				for (int i=0; i<lastHand.size(); i++) {
					Card card = lastHand.getCard(i);
					g.drawImage(cardImages[card.suit][card.rank], nameStartX + (cardOverlappingX * i), handStartY, this);
				}
			}
			
			
		}
		
		/**
		 * Private helper function to help determine which card was clicked (using formula used above)
		 * @param x X coordinate of mouse click
		 * @param y Y coordinate of mouse click
		 * @return integer value representing index of card clicked
		 */
		private int cardIdxClicked(int x, int y) {
			
			if (activePlayer < 0 || activePlayer >= game.getNumOfPlayers()) {
				return -1;
			}
			
			CardList playerCards = game.getPlayerList().get(activePlayer).getCardsInHand();
			
			for (int i = playerCards.size() - 1; i >= 0; i--) {
				int cardIStartX = cardStartX + (cardOverlappingX * i);
				int cardWidth = (i == playerCards.size() - 1 || selected[i]) ? this.cardWidth : cardOverlappingX;
				int cardIEndX = cardIStartX + cardWidth;
				
				int cardIStartY = cardStartY + spacingY*activePlayer;
				if (selected[i])
					cardIStartY += cardRaiseY;
				int cardIEndY = cardIStartY + 97;
				
				if (x >= cardIStartX && x <= cardIEndX && y >= cardIStartY && y <= cardIEndY)
					return i;
					
			}
			return -1;
		}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {
			// ignore mouse clicks by returning immediately if bigtwopanel is not enabled
			if (!bigTwoPanelEnabled) return;
			int clickedCard = cardIdxClicked(e.getX(), e.getY());
			if (clickedCard != -1) {
				selected[clickedCard] = !selected[clickedCard];
			}
			repaint();
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
	}

	/**
	 * Sets the index of the active player.
	 * 
	 * @param activePlayer the index of the active player (i.e., the player who can
	 *                     make a move)
	 */
	public void setActivePlayer(int activePlayer) {
		if (activePlayer < 0 || activePlayer >= game.getNumOfPlayers()) {
			this.activePlayer = -1;
		} else {
			this.activePlayer = activePlayer;
		}
	}

	/**
	 * Repaints the GUI by painting the components again
	 */
	@Override
	public void repaint() {
		frame.repaint();
		
	}
	
	/**
	 * Function to display a dialog box (for game over)
	 * @param message String message to display in dialog box
	 */
	public void displayDialog(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

	
	/**
	 * Prints out message to msgArea
	 * @param msg String message which we wish to display
	 */
	@Override
	public void printMsg(String msg) {
		msgArea.append(msg);
	}

	/**
	 * Function to clear the message area of GUI
	 */
	@Override
	public void clearMsgArea() {
		msgArea.setText("");
	}
	
	/**
	 * Function to print out player's chat message to chat area
	 * @param msg Chat message sent by player
	 */
	public void printChatMsg(String msg) {
		chatArea.append(msg);
	}

	
	/**
	 * Resets the card game user interface
	 */
	@Override
	public void reset() {
		resetSelected();
		clearMsgArea();
		chatArea.setText("");
		enable();
	}

	/**
	 * Enables user interactions (enable play, pass, chatinput as well as bigTwoPanel)
	 */
	@Override
	public void enable() {
		playButton.setEnabled(true);
		passButton.setEnabled(true);
		chatInput.setEnabled(true);
		bigTwoPanelEnabled = true;
	}
	
	/**
	 * Disables the table (panel and buttons)
	 */
	public void disableTable() {
		bigTwoPanelEnabled = false;
		playButton.setEnabled(false);
		passButton.setEnabled(false);
	}
	
	/**
	 * Enables the table (panel and buttons)
	 */
	public void enableTable() {
		bigTwoPanelEnabled = true;
		playButton.setEnabled(true);
		passButton.setEnabled(true);
	}

	
	/**
	 * Disables user interactions (by disabling play, pass, chatInput and bigTwoPanel)
	 */
	@Override
	public void disable() {
		playButton.setEnabled(false);
		passButton.setEnabled(false);
		chatInput.setEnabled(false);
		bigTwoPanelEnabled = false;
	}

	/**
	 * Prompts active player to select cards and make his/her move.
	 */
	public void promptActivePlayer() {
		String message = game.getPlayerList().get(activePlayer).getName() + "'s turn: \n";
		if (activePlayer == game.getPlayerID()) {
			message = "Your turn: \n";
		}
		printMsg(message);
	}
	
	/**
	 * Returns an array of indices of the cards selected through the UI.
	 * Adapted from Professor Kenneth Wong's code
	 * @return an array of indices of the cards selected, or null if no valid cards
	 *         have been selected
	 */
	private int[] getSelected() {
		
		int[] cardIdx = null;
		int count = 0;
		for (int j = 0; j < selected.length; j++) {
			if (selected[j]) {
				count++;
			}
		}

		if (count != 0) {
			cardIdx = new int[count];
			count = 0;
			for (int j = 0; j < selected.length; j++) {
				if (selected[j]) {
					cardIdx[count] = j;
					count++;
				}
			}
		}
		return cardIdx;
	}
	
	/**
	 * Resets the list of selected cards to an empty list.
	 */
	private void resetSelected() {
		for (int j = 0; j < selected.length; j++) {
			selected[j] = false;
		}
		repaint();
	}
	
	private class ConnectMenuItemListener implements ActionListener {

		/**
		 * Handles the restart functionality of game (call reset and start a new game with fresh deck)
		 * @param e ActionEvent
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			game.connect();
		}
		
	}
	
	private class QuitMenuItemListener implements ActionListener {

		/**
		 * Quits the game if quit menu item is pressed
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			game.disconnect();
			System.exit(0);
		}
		
	}
	
	private class chatInputHandler implements ActionListener {
		
		/**
		 * Handles the chat functionality of game (prints message out and clears input area)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String playerMessage = chatInput.getText();
			
			if (playerMessage != null && !playerMessage.trim().isEmpty()) {
				CardGameMessage message = new CardGameMessage(CardGameMessage.MSG, -1, playerMessage+"\n");
				game.sendMessage(message);
				chatInput.setText("");
				chatInput.requestFocus();
			}
		}
	}
	
	private class ClearChatMsgMenuItemListener implements ActionListener {
		/**
		 * Clears the chat area in gui
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			chatArea.setText("");
		}
	}
	
	private class PlayButtonListener implements ActionListener {

		/**
		 * Handles the play function by calling game.makeMove with selected cards 
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			int[] cardIdx = getSelected();
			if (cardIdx == null) return; // if no cards selected, make play button do nothing
			resetSelected();
			game.makeMove(activePlayer, cardIdx);
		}
		
	}
	
	private class PassButtonListener implements ActionListener {

		/**
		 * Handles pass function by calling game.makeMove with null cardIdx
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			resetSelected();
			game.makeMove(activePlayer, null);
		}
		
	}
}
