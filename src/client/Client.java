package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Client implements Runnable {

	private Socket clientSocket;
	private BufferedReader serverToClient;
	private PrintWriter clientToServer;
	private String name;
	private boolean isAuth = false;
	public ObservableList<String> chatLog;

	public Client(String ipAddress, int portNumber, String name) throws UnknownHostException, IOException {

		//send info to server, hopefully everything is same
		clientSocket = new Socket(ipAddress, portNumber);
		// Instantiate writers and readers to the socket
		serverToClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		clientToServer = new PrintWriter(clientSocket.getOutputStream(), true);
		chatLog = FXCollections.observableArrayList();
		this.name = name;			//send input name to server
		//clientToServer.println(name);

	}

	public boolean login(String password) {
		clientToServer.println(name + " " + password);

		try {
			final String inputFromServer = serverToClient.readLine();
			if ( inputFromServer != null ) {
				isAuth = true;
				return true;
			}
			else return false;
		} catch (IOException e)
		{
		}

		return false;
	}

	public boolean changePassword(String password, String newPassword) {
		clientToServer.println(name + " " + password + " " + newPassword);

		try {
			final String inputFromServer = serverToClient.readLine();
			if ( inputFromServer != null ) {
				isAuth = true;
				return true;
			}
			else return false;
		} catch (IOException e)
		{
		}

		return false;
	}

	public void closeSocket()
	{
		try {
			clientSocket.close();
		} catch (IOException e) {

		}
	}

	public void writeToServer(String input)
	{
		clientToServer.println(name + " : " + input);
	}

	public void run() {
		//loop never stops, chatLog keeps updating
		while (true) {
			try {
				final String inputFromServer = serverToClient.readLine();
				Platform.runLater(new Runnable()
				{
					public void run()
					{
						chatLog.add(inputFromServer);
					}
				});

			} catch (SocketException e) {	//if server closes, display message
				Platform.runLater(new Runnable() {
					public void run() {
						chatLog.add("Server is closed.");
					}

				});
				break;
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}