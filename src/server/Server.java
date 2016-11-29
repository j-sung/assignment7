package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.net.InetAddress;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Properties;
import java.io.FileOutputStream;
import java.io.FileInputStream;


public class Server implements Runnable {
	private int portNumber;
	private ServerSocket socket;
	public ObservableList<String> serverLog;
	public ObservableList<String> clientNames;
	private ArrayList<Socket> clients;
	private ArrayList<cThread> clientThreads;
	
	private Properties users;
	
	public Server(int portNumber) throws IOException 
	{
		this.portNumber = portNumber;
		loadUsers();
		serverLog = FXCollections.observableArrayList();
		clientNames = FXCollections.observableArrayList();
		clients = new ArrayList<Socket>();
		clientThreads = new ArrayList<cThread>();
		socket = new ServerSocket(portNumber);
		
	}

	public void startServer() {
		try {
			socket = new ServerSocket(this.portNumber);
			serverLog = FXCollections.observableArrayList();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() 
	{
		try {
			
			try {			//find computer's IP Address and portNumber
				InetAddress iAddress = InetAddress.getLocalHost();
				serverLog.add("Server IP address : " +iAddress.getHostAddress());
			} catch (UnknownHostException e2) {
				e2.printStackTrace();
			}
			serverLog.add("Server Port : " + portNumber);
			
			//loop keeps running to get more clients
			while (true) {
				//server waits for clients to add
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						serverLog.add("Waiting for client...");
					}
				});

				final Socket clientSocket = socket.accept();

				// add the incoming socket connection to the list of clients
				clients.add(clientSocket);

				Platform.runLater(new Runnable() 
				{
					@Override
					public void run() 
					{
						serverLog.add("Client" + clientSocket.getRemoteSocketAddress()
								+ " connected");
					}
				});
				cThread clientThreadHolderClass = new cThread(clientSocket, this);
				Thread clientThread = new Thread(clientThreadHolderClass);
				clientThreads.add(clientThreadHolderClass);
				clientThread.start();
				ServerMain.threads.add(clientThread);
			}
		}  catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void clientDisconnected(cThread client) {

		Platform.runLater(new Runnable() 
		{
			@Override
			public void run() {
				serverLog.add("Client "+ client.getcSocket().getRemoteSocketAddress()
						+ " disconnected");
				clients.remove(clientThreads.indexOf(client));
				clientNames.remove(clientThreads.indexOf(client));
				clientThreads.remove(clientThreads.indexOf(client));
			}
		});
		
		
	}

	public void updateAllSockets(String input) 
	{
		for (cThread clientThread : clientThreads) 
		{
			clientThread.writeToServer(input);
		}
	}
	
	private void loadUsers()
	{
		FileInputStream userFile = null;
		
		try	{
			users = new Properties();
			userFile = new FileInputStream("username/password");
			users.load(userFile);
		} catch (IOException io) {
			System.out.println("users.properties cannot be found.  Use user1, user2, or Justin instead.");
			
			users.setProperty("user1", "user1");		//first= user, second=pw
			users.setProperty("user2", "user2");
			users.setProperty("Justin", "pikachu");		//store these info on stream
			
		} finally {
			if (userFile != null) {
				try {
					userFile.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}				
		}
	}
	
	protected void saveUsers(String name, String password)
	{
		users.setProperty(name, password);
		
		FileOutputStream userFile = null;
		
		try
		{
			userFile = new FileOutputStream("username/password");
			users.store(userFile, null);
		
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (userFile != null) {
				try {
					userFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	
		}
	}
	
	public boolean auth(String user, String passwd)
	{
	    if (passwd == null) return false;
	    else 
	    {
	    	String tmp = users.getProperty(user);
	    	if (passwd.equals(tmp))
	    		return true;
	    	else
	    		return false;
	    }
	}
}
