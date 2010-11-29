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
	//Admins and Operators should also have stuff to change permission
	//Artists and higher should have ability to lock current section, if it's not already locked
	//have a place to display the owner of the section if there is an owner.
	public AdminPanel(ClientState state) {
		String[] data = {"this", "is", "a", "test"};
		JLabel users = new JLabel ("List of active users.");
		activeusers = new JList(data);
		activeusers.setPrototypeCellValue("Index 1234567890");
		addUserButton.setPreferredSize(new Dimension(200,50));
		getInformation.setPreferredSize(new Dimension(200,50));
		addUserButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				if(e.getActionCommand().equals("Add User") ){
					JFrame userWind = new JFrame("Title");
					userWind.setSize(100,100);
					userWind.setVisible(true);
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
}
