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
/**
 *  The Panel used for administrative options. Only available if the user has permission.
 * 
 * @author Mark Wyrzykowski
 * @since 112310
 *
 */
public class AdminPanel extends JPanel {
	private JList activeusers; //this list will contain the list of users logged in to the server. Double clicking brings up info
	private JTextField lookupuser; //so you can try to find a specific user
	private Client parent;
	private JButton getInformation  = new JButton("Get User Information"); //should retrieve information about the user in lookupuser. Same info should pop up from double clicking on a name in
	private Vector<String> userNames = new Vector<String>();
	private JButton addUserButton = new JButton("Register User");
	private JButton kickUser = new JButton("Kick User");
	private JButton banUser = new JButton("Ban User");
	private JButton pardonUser = new JButton("Pardon User");
	//Admins and Operators should also have stuff to change permission
	//Artists and higher should have ability to lock current section, if it's not already locked
	//have a place to display the owner of the section if there is an owner.
	public AdminPanel(ClientState state) {
		final ClientState stat = state;
		JLabel users = new JLabel("List of active users.");
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
							if(e.getActionCommand().equals("Confirm") && new String(userPass.getPassword()).equals(new String(confirmPass.getPassword())) && !(userName.getText().equals("")) && !(new String(userPass.getPassword()).equals(""))){
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

		kickUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if(e.getActionCommand().equals("Kick")){
						kick(userName.getSelectedText(), stat);
				}
			}
		});
		banUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if(e.getActionCommand().equals("Kick")){
						ban(userName.getSelectedText(), stat);
				}
			}
		});
		pardonUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if(e.getActionCommand().equals("Kick")){
						pardon(userName.getSelectedText(), stat);
				}
			}
		});

		this.setPreferredSize(new java.awt.Dimension(200,300));
//		parent.getCanvas();
		this.add(addUserButton);
		this.add(getInformation);
		this.add(users);
		this.add(activeusers);
		this.add(kickUser);
		this.add(banUser);
		this.add(pardonUser);
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
	public void kick(String user, ClientState state){
		state.canvas.socket.kickUser(user);
	}
	public void ban(String user, ClientState state){
		state.canvas.socket.banUser(user);
	}
	public void pardon(String user, ClientState state){
		state.canvas.socket.pardonUser(user);
	}
}
