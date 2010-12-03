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
	//Admins and Operators should also have stuff to change permission
	//Artists and higher should have ability to lock current section, if it's not already locked
	//have a place to display the owner of the section if there is an owner.
	public AdminPanel(ClientState state) {
		this.state = state;
		JLabel users = new JLabel("List of active users.");
		
		activeusers = new JList(userNames);
		activeusers.setPrototypeCellValue("Index 1234567890");
		Dimension buttonDimension = new Dimension(200, 50);
		registerUser.setPreferredSize(buttonDimension);
		kickUser.setPreferredSize(buttonDimension);
		banUser.setPreferredSize(buttonDimension);
		pardonUser.setPreferredSize(buttonDimension);
		modifyUserPermissions.setPreferredSize(buttonDimension);
		modifyUserPassword.setPreferredSize(buttonDimension);
		deleteUser.setPreferredSize(buttonDimension);
		getInformation.setPreferredSize(buttonDimension);

		registerUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("Register User") ) {
					final JFrame userWind = new JFrame("Register Yourself");
					userWind.setSize(200,250);

					final JTextField userName = new JTextField(15);

					final JPasswordField userPass = new JPasswordField(15);
					final JPasswordField confirmPass = new JPasswordField(15);
					userPass.setEchoChar('*');
					confirmPass.setEchoChar('*');

					final JButton confirmUser = new JButton("Confirm");

					userWind.getContentPane().setLayout(new java.awt.FlowLayout());
					userWind.add(new JLabel("Enter User Name"));
					userWind.add(userName);
					userWind.add(new JLabel("Enter Password"));
					userWind.add(userPass);
					userWind.add(new JLabel("Re-Enter Password"));
					userWind.add(confirmPass);
					userWind.add(confirmUser);

					userWind.setVisible(true);
					confirmUser.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if(
								e.getActionCommand().equals("Confirm") &&
								new String(userPass.getPassword()).equals(new String(confirmPass.getPassword())) &&
								!userName.getText().equals("") &&
								!(new String(userPass.getPassword()).equals(""))
							) {
								addUser(userName.getText(), new String(confirmPass.getPassword()));
								userWind.setVisible(false);
							}
						}
					});
				}
			}
		});
		kickUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if(e.getActionCommand().equals("Kick User")){
						kick((String)activeusers.getSelectedValue());
				}
			}
		});
		banUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if(e.getActionCommand().equals("Ban User")){
						ban((String)activeusers.getSelectedValue());
				}
			}
		});
		pardonUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if(e.getActionCommand().equals("Pardon User")){
						pardon((String)activeusers.getSelectedValue());
				}
			}
		});
		modifyUserPermissions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("Change Permissions for User") ) {
					String permissions = JOptionPane.showInputDialog(
						this,
						"Enter new permissions for " + activeusers.getSelectedValue()
					);
					modifyUserPermissions((String)activeusers.getSelectedValue(), permissions);
				}
			}
		});
		modifyUserPassword.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("Change Password for User") ) {
					final JFrame userWind = new JFrame("Change Password for " + activeusers.getSelectedValue());
					userWind.setSize(200,250);

					final JPasswordField userPass = new JPasswordField(15);
					final JPasswordField confirmPass = new JPasswordField(15);
					userPass.setEchoChar('*');
					confirmPass.setEchoChar('*');

					final JButton confirmUser = new JButton("Confirm");

					userWind.getContentPane().setLayout(new java.awt.FlowLayout());
					userWind.add(new JLabel("Enter New Password"));
					userWind.add(userPass);
					userWind.add(new JLabel("Re-Enter Password"));
					userWind.add(confirmPass);
					userWind.add(confirmUser);

					userWind.setVisible(true);
					confirmUser.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if(
								e.getActionCommand().equals("Confirm") &&
								new String(userPass.getPassword()).equals(new String(confirmPass.getPassword())) &&
								!(new String(userPass.getPassword()).equals(""))
							) {
								modifyUserPassword((String)activeusers.getSelectedValue(), new String(confirmPass.getPassword()));
								userWind.setVisible(false);
							}
						}
					});
				}
			}
		});
		deleteUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("Delete User") ) {
					int result = JOptionPane.showConfirmDialog(
						null,
						"Please confirm deletion of " + activeusers.getSelectedValue(),
						"Confirm",
						JOptionPane.OK_CANCEL_OPTION
					);
					if(result == JOptionPane.OK_OPTION)
						deleteUser((String)activeusers.getSelectedValue());
				}
			}
		});
		getInformation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("Get User Information") ) {
					getUserInformation((String)activeusers.getSelectedValue());
				}
			}
		});

		setPreferredSize(new java.awt.Dimension(200,300));
		add(users);
		add(activeusers);

		add(registerUser);
		add(kickUser);
		add(banUser);
		add(pardonUser);
		add(modifyUserPermissions);
		add(modifyUserPassword);
		add(deleteUser);
		add(getInformation);
	}

	private ClientState state;
	private JList activeusers; //this list will contain the list of users logged in to the server. Double clicking brings up info
	private JTextField lookupuser; //so you can try to find a specific user
	private Client parent;
	private Vector<String> userNames = new Vector<String>();
	private JButton registerUser = new JButton("Register User");
	private JButton kickUser = new JButton("Kick User");
	private JButton banUser = new JButton("Ban User");
	private JButton pardonUser = new JButton("Pardon User");
	private JButton modifyUserPermissions = new JButton("Change Permissions for User");
	private JButton modifyUserPassword = new JButton("Change Password for User");
	private JButton deleteUser = new JButton("Delete User");
	private JButton getInformation  = new JButton("Get User Information");

	public void addUser(String user) {
		if(user == null) return;
		userNames.add(user);
		activeusers.setListData(userNames);
	}

	public void removeUser(String user) {
		for(int i = 0; i < userNames.size(); i++){
			if(userNames.get(i).equals(user)){
				userNames.remove(i);
				break;
			}
		}
		activeusers.setListData(userNames);
	}

	public void clearUserList() {
		userNames.removeAllElements();
		activeusers.setListData(userNames);
	}

	/// HERE THERE BE ADMINISTRATIVE FUNCTION HANDLES ///

	public void addUser(String user, String password) {
		state.canvas.socket.registerUser(user, password);
	}

	public void kick(String user) {
		state.canvas.socket.kickUser(user);
	}

	public void ban(String user) {
		state.canvas.socket.banUser(user);
	}

	public void pardon(String user) {
		state.canvas.socket.pardonUser(user);
	}

	public void modifyUserPermissions(String user, String permissions) {
		state.canvas.socket.modifyUserPermissions(user, permissions);
	}

	public void modifyUserPassword(String user, String password) {
		state.canvas.socket.modifyUserPassword(user, password);
	}

	public void deleteUser(String user) {
		state.canvas.socket.deleteUser(user);
	}

	public void getUserInformation(String user) {
		state.canvas.socket.requestUserPermissions(user);
	}
}
