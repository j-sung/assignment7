package client;

import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Application;
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

public class ClientMain extends Application 
{
	private ArrayList<Thread> threads;
	public static void main(String[] args)
	{
		launch();
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		for (Thread thread: threads){
			thread.interrupt();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		threads = new ArrayList<Thread>();
		primaryStage.setTitle("JSung's Fantastic Chat");
		primaryStage.setScene(makePanel(primaryStage));
		primaryStage.show();
	}

	public Scene makePanel(Stage primaryStage) 
	{	
		GridPane root = new GridPane();
		root.setVgap(10);
		root.setHgap(10);
		root.setAlignment(Pos.CENTER);

		TextField nameField = new TextField();
		TextField ipAddressField = new TextField();
		TextField portNumberField = new TextField();

		Label nameLabel = new Label("Name ");
		Label ipAddressLabel = new Label("ipAddress ");
		Label portNumberLabel = new Label("Port Number");
		Label errorLabel = new Label();
		
		Button enter = new Button("Enter JSung's server");
		enter.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent Event) {
				Client client;
				try {
					client = new Client(ipAddressField.getText(), Integer.parseInt(portNumberField.getText()), nameField.getText());
					Thread clientThread = new Thread(client);
					clientThread.start();
					threads.add(clientThread);
					
					//close log-in screen and bring up chatBox
					primaryStage.close();
					primaryStage.setScene(chatBox(client));
					primaryStage.show();
				}
				catch (NumberFormatException | IOException e) {
					errorLabel.setTextFill(Color.RED);
					errorLabel.setText("Invalid Port Number, Try Again!!");
				}
				
			}
		});

		root.add(nameField, 0, 0);
		root.add(nameLabel, 1, 0);
		root.add(ipAddressField, 0, 1);
		root.add(ipAddressLabel, 1, 1);
		root.add(portNumberField, 0, 2);
		root.add(portNumberLabel, 1, 2);
		root.add(enter, 0, 3, 2, 1);
		root.add(errorLabel, 0, 4);
		// update chatBox
		return new Scene(root, 400, 400);
	}

	public Scene chatBox(Client client) {
		// make the chatBox
		GridPane root = new GridPane();
		root.setAlignment(Pos.CENTER);
		root.setHgap(10);
		root.setVgap(10);

		ListView<String> chatListView = new ListView<String>();
		chatListView.setItems(client.chatLog);

		// make chat box and send text to server
		TextField chatTextField = new TextField();
		chatTextField.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) 
			{
				client.writeToServer(chatTextField.getText());
				chatTextField.clear();
			}
		});

		// add the components into the chatBox
		root.add(chatListView, 0, 0);
		root.add(chatTextField, 0, 1);

		//update window
		return new Scene(root, 400, 400);

	}
}