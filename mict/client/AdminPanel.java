package mict.client;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JList;
public class AdminPanel extends JPanel {
	private JList activeusers; //this list will contain the list of users logged in to the server. Double clicking brings up info
	private JTextField lookupuser; //so you can try to find a specific user
	private Client parent;
	private JButton addUserButton; //should pop up a Window to enter information
	private JButton getInformation; //should retrieve information about the user in lookupuser. Same info should pop up from double clicking on a name in 
	//Admins and Operators should also have stuff to change permission
	//Artists and higher should have ability to lock current section, if it's not already locked
	//have a place to display the owner of the section if there is an owner.
	
}
