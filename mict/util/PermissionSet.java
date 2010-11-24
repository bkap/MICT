package mict.util;

import java.io.*;
import java.util.*;

import mict.networking.*;

public class PermissionSet extends Hashtable<String, Permission> {
	public PermissionSet(ProtocolSource parent) {
		this.parent = parent;
		read();
	}

	private ProtocolSource parent = null;

	public void setParent(ProtocolSource parent) {
		this.parent = parent;
	}

	public Permission setPermission(Permission p) {
		Permission prev = remove(p.getKey());
		put(p.getKey(), p);
		return prev;
	}

	public Permission setPermission(String p) {
		int index = p.lastIndexOf('.');
		String value = "";
		if(index >= 0) {
			value = p.substring(index+1);
			p = p.substring(0,index);
		}
		return setPermission(new Permission(p, value));
	}

	public boolean capableOf(String action) {
		int index = action.lastIndexOf('.');
		String value = "";
		if(index >= 0) {
			value = action.substring(index+1);
			action = action.substring(0,index);
		}
		return get(action).capableOf(value);
	}

	public void read() {
		parent.refreshPermissions();
	}

	public void write() {
		try {
			OutputStream out = parent.getOutputStream();
			Iterator<Permission> i = values().iterator();
			out.write(parent.getPrefix().getBytes());
			EscapingOutputStream eout = new EscapingOutputStream(out);
			while(i.hasNext()) {
				out.write(parent.getSeparator().getBytes());
				ObjectOutputStream oout = new ObjectOutputStream(eout);
				oout.writeObject(i.next());
				eout.flush();
			}
			out.write("\n".getBytes());
			out.flush();
		} catch(IOException e) {
			System.err.println("Could not write permissions to target given by " + parent);
			e.printStackTrace(System.err);
		}
	}
}
