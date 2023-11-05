package View;

import Server.Server;
import javafx.application.Application;

public class Main {

	/**
	 * Launches both the player and the server.
	 * @param args
	 */
	public static void main(String[] args) {
		if (args[0].equals("client"))
			PlayerView.Init();
		else if(args[0].equals("server")) {
			Server x = new Server();
		}
	}

}
