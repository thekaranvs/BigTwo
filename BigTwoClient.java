import java.io.*;
import java.net.Socket;

import javax.swing.JOptionPane;

/**
 * BigTwoClient used to model a Big Two game client
 * that is responsible for establishing a connection and communicating with the Big Two game server
 * @author karanvs
 *
 */
public class BigTwoClient implements NetworkGame {
	
	private BigTwo game;
	private BigTwoGUI gui;
	
	/**
	 * a socket connection to the game serve
	 */
	private Socket sock;
	
	private ObjectOutputStream oos;
	private int playerID;
	private String playerName;
	private String serverIP;
	private int serverPort;
	
	// ObjectInputStream to receive data from server
	private ObjectInputStream ois;
	
	/**
	 * a constructor for creating a Big Two client
	 * @param game The BigTwo game object its associated with
	 * @param gui The BigTwoGUI object associated with the game object
	 */
	public BigTwoClient(BigTwo game, BigTwoGUI gui) {
		this.game = game;
		this.gui = gui;
		
		playerID = -1;
		playerName = (String) JOptionPane.showInputDialog("Enter your name: ");
		if (playerName == null || playerName.trim().length() == 0) {
			playerName = "Anonymous";
		}
		
		serverIP = "127.0.0.1";
		serverPort = 2396;
		
		gui.disableTable();
		connect();
	}

	/**
	 *  a method for getting the playerID (i.e., index) of the local player
	 */
	public int getPlayerID() {
		return this.playerID;
	}

	/**
	 * a method for setting the playerID (i.e., index) of the local player
	 */
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	/**
	 * a method for getting the name of the local player.
	 */
	public String getPlayerName() {
		return this.playerName;
	}

	/**
	 * a method for setting the name of the local player
	 * @param playerName The name of player
	 */
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	/**
	 * a method for getting the IP address of the game server
	 */
	public String getServerIP() {
		return this.serverIP;
	}

	/**
	 * a method for setting the IP address of the game server
	 * @param serverIP The IP address of the game server
	 */
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	/**
	 * a method for getting the TCP port of the game server
	 */
	public int getServerPort() {
		return this.serverPort;
	}

	/**
	 * a method for setting the TCP port of the game server
	 * @param serverPort The int TCP port of the game server
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * a method for making a socket connection with the game server
	 */
	public void connect() {
		
		try {
			if (sock == null) {
				sock = new Socket(serverIP, serverPort);
				
				oos = new ObjectOutputStream(sock.getOutputStream());
				ois = new ObjectInputStream(sock.getInputStream());
				
				Thread messageThread = new Thread(new ServerHandler());
				messageThread.start();
				
				gui.reset();
				gui.disableTable();
				gui.printMsg("Connected to server at /" + serverIP + ":" + serverPort + "\n");
				gui.repaint();
			} else {
				gui.displayDialog("Already connected to sever!\n");
			}
		} catch (Exception e) {
			gui.printMsg("UNABLE TO CONNECT TO GAME SERVER!");
			sock = null;
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Function to close the socket connection to server
	 */
	public void disconnect() {
		try {
			if (sock != null) {
				sock.close();
			}
			sock = null;
		} catch (Exception e) {
			gui.printMsg("ERROR DISCONNECTING FROM SERVER!\n");
			e.printStackTrace();
		}
	}

	/**
	 * a method for parsing the messages received from the game server
	 * @param message The GameMessage object received from game server
	 */
	public synchronized void parseMessage(GameMessage message) {
		
		switch(message.getType()) {
		
		case CardGameMessage.PLAYER_LIST:
			setPlayerID(message.getPlayerID());
			game.getPlayerList().get(playerID).setName(playerName);
			gui.repaint();
			
			String[] playerNames = (String[]) message.getData();
			for (int i=0; i<game.getNumOfPlayers(); i++) {
				if (playerNames[i] != null) {
					game.playerPresent[i] = true;
					game.getPlayerList().get(i).setName(playerNames[i]);
				}
			}
			
			sendMessage(new CardGameMessage(CardGameMessage.JOIN, -1, playerName));
			break;
		
		case CardGameMessage.FULL:
			gui.printMsg("GAME IS FULL - CONNECTION TERMINATED!\n");
			gui.displayDialog("GAME IS FULL - CONNECTION TERMINATED!\n");
			disconnect();
			break;
		
		case CardGameMessage.QUIT:
			game.playerPresent[message.getPlayerID()] = false;
			gui.printMsg(game.getPlayerList().get(message.getPlayerID()).getName() + " (" +(String)message.getData() + ") quit the game.\n");
			// make player who quit name empty
			game.getPlayerList().get(message.getPlayerID()).setName("");
			
			// if game in progress, stop the game
			if (!game.endOfGame()) {
				for (int i=0; i<4; i++) {
					game.getPlayerList().get(i).removeAllCards();
				}
				gui.disableTable();
				sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
				gui.repaint();
			}
			gui.repaint();
			break;
		
		case CardGameMessage.READY:
			gui.printMsg(game.getPlayerList().get(message.getPlayerID()).getName() + " is ready!\n");
			break;
		
		case CardGameMessage.START:
			gui.clearMsgArea();
			gui.enable();
			gui.printMsg("All players have joined! Game starts!\n");
			game.start((BigTwoDeck) message.getData());
			break;
		
		case CardGameMessage.MOVE:
			game.checkMove(message.getPlayerID(), (int[])message.getData());
			break;
			
		case CardGameMessage.MSG:
			gui.printChatMsg((String)message.getData());
			break;
			
		case CardGameMessage.JOIN:
			game.playerPresent[message.getPlayerID()] = true;
			if (message.getPlayerID() == this.getPlayerID()) {
				sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
				gui.repaint();
			} else {
				game.getPlayerList().get(message.getPlayerID()).setName((String)message.getData());
				gui.printMsg((String)message.getData() + " has joined the game!\n");
				gui.repaint();
			}
			break;
		}
		
	}

	/**
	 * a method for sending the specified message to the game server
	 */
	public synchronized void sendMessage(GameMessage message) {
		
		try {
			oos.writeObject(message);
		} catch (Exception e) {
			gui.printMsg("UNABLE TO SEND GameMessage OBJECT TO SERVER!");
			e.printStackTrace();
		}
	}
	
	/**
	 * an inner class that implements the Runnable interface to receive messages from server
	 * @author karanvs
	 *
	 */
	private class ServerHandler implements Runnable {

		/**
		 * Used to receive messages from server and call parseMessage for further handling
		 */
		@Override
		public void run() {
			try {
				GameMessage message;
				while ((sock != null) && (message = (GameMessage) ois.readObject()) != null) {
					parseMessage(message);
				}
				
			} catch(Exception e) {
				gui.printMsg("UNABLE TO RECEIVE MESSAGE FROM SERVER! TRY AGAIN LATER\n");
				try {
					sock.close();
				} catch(Exception ex) {
					System.out.println("Unable to close socket!");
				}
				sock = null;
				System.out.println("Error: Unable to receive message from server. Printing stack trace...\n");
				e.printStackTrace();
			}
			
		}
		
	}

}
