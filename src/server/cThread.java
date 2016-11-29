package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import javafx.application.Platform;

/* Thread Class for each incoming client */
public class cThread implements Runnable {

	private BufferedReader incomingMessage;
	private PrintWriter outgoingMessage;
	private Socket cSocket;		// client's socket
	private Server server;		//server
	private String client;		// The name of the client

	public cThread(Socket clientSocket, Server baseServer) {
		this.setcSocket(clientSocket);
		this.server = baseServer;
		try {
			 // reads all incoming messages that the client passes to the server
			incomingMessage = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			//writes outgoing messages from the server to the client
			outgoingMessage = new PrintWriter(
					clientSocket.getOutputStream(), true);

		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			this.client = getClientName();
			Platform.runLater(new Runnable() 
			{
				@Override
				public void run() 
				{
					server.clientNames.add(client + " -> "+ cSocket.getRemoteSocketAddress());
					server.updateAllSockets("null");
				}

			});
			String inputToServer;
			while (true) {
				inputToServer = incomingMessage.readLine();
				if (inputToServer != null) server.updateAllSockets(inputToServer);
			}
		} catch (SocketException e) {		//if client disconnects
			server.clientDisconnected(this);

		} catch (IOException e) 
		{
			//e.printStackTrace();
		}
	}

	public void writeToServer(String input)
	{
		outgoingMessage.println(input);
	}

	public String getClientName() throws IOException 
	{
		String in = incomingMessage.readLine();
		int sep = in.indexOf(" ");
		int secondSpace = in.indexOf(" ", sep+1);
		
		String name = in.substring(0, sep);
		if (secondSpace < 0)
		{
			String passwd = in.substring(sep+1);
					
			System.out.println("user="+name+", password="+passwd);
			
			if ( ! server.auth(name, passwd) )
			{
				//server.clientDisconnected(this);
				System.out.println(name + " auth failed");
				cSocket.close();
			}
			else
				System.out.println(name + " authenticated successfully");
			
		} else {
			String passwd = in.substring(sep+1, secondSpace);
			String newPasswd = in.substring(secondSpace+1);
			
			System.out.println("user="+name+", password="+passwd+", new password="+newPasswd);
			
			if ( ! server.auth(name, passwd) )
			{
				System.out.println(name + " auth failed");
				cSocket.close();
			}
			else
			{
				System.out.println(name + " authenticated successfully");
				server.saveUsers(name, newPasswd);
				System.out.println(name + " changed password successfully");
			}
		}
		
		return name;		//get name of client
	}

	public String getClient() 
	{
		return this.client;
	}

	public Socket getcSocket() 	//get client socket
	{
		return cSocket;
	}

	public void setcSocket(Socket clientSocket) //set client socket
	{
		this.cSocket = clientSocket;
	}
}