package client;

import java.io.IOException;
import java.net.ConnectException;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ClientMain extends Application
{
	private ArrayList<Thread> threads;
	private Client client;

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
		primaryStage.setScene(makePopUp(primaryStage));
		primaryStage.show();
	}

	public Scene makePopUp(Stage primaryStage)
	{
		GridPane root = new GridPane();
		root.setAlignment(Pos.CENTER);

		TextField nameField = new TextField();
		PasswordField passwdField = new PasswordField();
		PasswordField newPasswdField = new PasswordField();
		CheckBox changePasswd = new CheckBox();
		TextField ipAddressField = new TextField();
		TextField portNumberField = new TextField();

		Label nameLabel = new Label("Name");
		Label passwdLabel = new Label("Password ");
		Label newPasswdLabel = new Label("New Password ");
		Label changePasswdLabel = new Label("Change Password?");
		Label ipAddressLabel = new Label("IP Address");
		Label portNumberLabel = new Label("Port Number");
		Label error = new Label();
		error.setTextFill(Color.RED);

		Button enter = new Button("Connect to JSung Chat server");
		enter.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent Event) {
				try {
					client = new Client(ipAddressField.getText(), Integer.parseInt(portNumberField.getText()),
							nameField.getText());

					if (changePasswd.isSelected()) {

						if (client.changePassword(passwdField.getText(), newPasswdField.getText())) {
							Thread clientThread = new Thread(client);
							clientThread.start();
							threads.add(clientThread);

							primaryStage.close();
							primaryStage.setScene(chatBox(client));
							primaryStage.show();
						}
						else
						{
							error.setTextFill(Color.RED);
							error.setText("Invalid user or password. Try Again!!");
						}
					}
					else
					{
						if (client.login(passwdField.getText())) {
							Thread clientThread = new Thread(client);
							clientThread.start();
							threads.add(clientThread);

							primaryStage.close();
							primaryStage.setScene(chatBox(client));
							primaryStage.show();

						}
						else
						{
							error.setTextFill(Color.RED);
							error.setText("Invalid user or password. Try Again!!");
						}
					}
				}
				catch(ConnectException e)
				{
					error.setText("Invalid IP Address, Try Again!!");
				}
				catch (IOException e)
				{
					error.setText("Invalid Port Number, Try Again!!");
				}
				catch(NumberFormatException e)
				{
					error.setText("Invalid Port Number, Try Again!!");
				}

			}
		});

		root.add(nameField, 0, 0);
		root.add(nameLabel, 1, 0);
		root.add(passwdField, 0, 1);
		root.add(passwdLabel, 1, 1);
		root.add(newPasswdField, 0, 2);
		root.add(newPasswdLabel, 1, 2);
		root.add(changePasswd, 0, 3);
		root.add(changePasswdLabel, 1, 3);
		root.add(ipAddressField, 0, 4);
		root.add(ipAddressLabel, 1, 4);
		root.add(portNumberField, 0, 5);
		root.add(portNumberLabel, 1, 5);
		root.add(enter, 0, 6, 2, 1);
		root.add(error, 0, 7);
		// update chatBox
		return new Scene(root, 400, 400);
	}

	public Scene chatBox(Client client) {
		// make the chatBox
		GridPane root = new GridPane();
		root.setAlignment(Pos.CENTER);

		Label chatLabel = new Label("Chat box");
		Label infoLabel = new Label("Press Enter \n"
				+ "to send");
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
		root.add(chatListView, 1, 0);
		root.add(chatLabel, 0, 0);
		root.add(infoLabel, 0, 1);
		root.add(chatTextField, 1, 1);

		return new Scene(root, 400, 500);		//update window constantly

	}
}