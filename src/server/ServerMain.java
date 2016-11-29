package server;

import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class ServerMain extends Application {
	public static ArrayList<Thread> threads;
	public static void main(String[] args){
		launch();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		threads = new ArrayList<Thread>();
		primaryStage.setTitle("JSung Chat Server");
		primaryStage.setScene(makePopUp(primaryStage));
		primaryStage.show();
		
	}

	
	public Scene makePopUp(Stage primaryStage) 
	{
		GridPane rootPane = new GridPane();
		rootPane.setAlignment(Pos.CENTER);

		Label portText = new Label();
		portText.setText("Port Number");
		Label error = new Label();
		error.setTextFill(Color.RED);
		TextField portTextField = new TextField();
		Button enter = new Button("Start");
		
		enter.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) 
			{
				try {
					Server server = new Server(Integer.parseInt(portTextField
							.getText()));
					Thread serverThread = (new Thread(server));
					serverThread.setName("Server Thread");
					serverThread.start();
					threads.add(serverThread);
					//closes pop up box and serverBox loads
					primaryStage.hide();
					primaryStage.setScene(serverBox(server));
					primaryStage.show();
				}catch(IllegalArgumentException e)
				{
					error.setText("Invalid Port Number!!! Try Again!!");
				}
				catch (IOException e) 
				{
					
				}
				
			}
		});

		rootPane.add(portText, 0, 0);
		rootPane.add(portTextField, 0, 1);
		rootPane.add(enter, 0, 2);
		rootPane.add(error, 0, 3);
		//update serverBox
		return new Scene(rootPane, 400, 300);
	}
	public Scene serverBox(Server server){
		// make Server Box
		GridPane root = new GridPane();
		root.setAlignment(Pos.CENTER);
		
		// list out the server log
		Label serverLogLabel = new Label("Server Log");
		ListView<String> serverLogView = new ListView<String>();
		ObservableList<String> serverLogList = server.serverLog;
		serverLogView.setItems(serverLogList);
		
		//list out the clients connected
		Label clientLabel = new Label("Clients Connected");
		ListView<String> cView = new ListView<String>();
		ObservableList<String> cList = server.clientNames;
		cView.setItems(cList);
		
		// add the components into the server Box
		root.add(serverLogLabel, 0, 1);
		root.add(serverLogView, 0, 2);
		root.add(clientLabel, 0, 3);
		root.add(cView, 0, 4);
		
		return new Scene(root, 400, 600);		//update window constantly
	}
}