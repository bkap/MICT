package mict.client;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.Dimension;
import javax.swing.*;
import java.util.*;

public class AdminPanel extends JPanel {
	private JList activeusers; //this list will contain the list of users logged in to the server. Double clicking brings up info
	private JTextField lookupuser; //so you can try to find a specific user
	private Client parent;
	private JButton addUserButton = new JButton("Add User"); //should pop up a Window to enter information
	private JButton getInformation  = new JButton("Get User Information"); //should retrieve information about the user in lookupuser. Same info should pop up from double clicking on a name in
	private Vector userNames = new Vector(20);
	//Admins and Operators should also have stuff to change permission
	//Artists and higher should have ability to lock current section, if it's not already locked
	//have a place to display the owner of the section if there is an owner.
	public AdminPanel(ClientState state) {
		JLabel users = new JLabel ("List of active users.");
		final JButton confirmUser = new JButton("Confirm");
		final JTextField userName = new JTextField(15);
		final JPasswordField userPass = new JPasswordField(15);
		userPass.setEchoChar('*');
		final JPasswordField confirmPass = new JPasswordField(15);
		confirmPass.setEchoChar('*');
		
		activeusers = new JList(userNames);
		activeusers.setPrototypeCellValue("Index 1234567890");
		addUserButton.setPreferredSize(new Dimension(200,50));
		getInformation.setPreferredSize(new Dimension(200,50));
		addUserButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				if(e.getActionCommand().equals("Add User") ){
					final JFrame userWind = new JFrame("Title");
					userWind.setSize(200,250);
					userWind.add(new JLabel("Enter User Name"));
					userWind.add(userName);
					userWind.add(new JLabel("Enter Password"));
					userWind.add(userPass);
					userWind.add(new JLabel("Re-Enter Password"));
					userWind.add(confirmPass);
					userWind.add(confirmUser);
					userWind.getContentPane().setLayout(new java.awt.FlowLayout());
					userWind.setVisible(true);
					confirmUser.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e){
							if(e.getActionCommand().equals("Confirm") && userPass.getText().equals(confirmPass.getText()) && !(userName.getText().equals("")) && !(userPass.getText().equals(""))){
								addUser(userName.getText());
								userName.setText("");
								userPass.setText("");
								confirmPass.setText("");
								userWind.setVisible(false);
							}
						}
					});
				}
			}

		});
		this.setPreferredSize(new java.awt.Dimension(200,300));
//		parent.getCanvas();
		this.add(addUserButton);
		this.add(getInformation);
		this.add(users);
		this.add(activeusers);
		/* if(user is an admin){
		 *		this.add(kick button);
		 * }
		 */
	}

	public void addUser(String user){
		userNames.add(user);
		activeusers.setListData(userNames);
	}

	public void removeUser(String user){
		for(int i = 0; i < userNames.size(); i++){
			if(userNames.get(i).equals(user)){
				userNames.remove(i);
				break;
			}
		}
		activeusers.setListData(userNames);
	}
}