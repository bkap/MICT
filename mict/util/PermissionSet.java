package mict.util;

import java.io.*;
import java.util.*;

import mict.networking.*;

public class PermissionSet extends Hashtable<String, Permission> {
	public PermissionSet() {}

	private boolean updated = false;

	public boolean isUnsaved() {
		return updated;
	}

	public void setSaved() {
		updated = false;
	}

	public Permission setPermission(Permission p) {
		Permission prev = remove(p.getKey());
		put(p.getKey(), p);
		return prev;
	}

	public Permission setPermission(String p) {
		updated = true;
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

	public void read(String input, String prefix, String separator, boolean purge) {
		if(purge) clear();
		if(input.startsWith(prefix)) input = input.substring(prefix.length());
		int index = input.indexOf(separator);
		while(index >= 0) {
			String p = input.substring(0, index).trim();
			if(!p.equals(""))
				setPermission(Permission.parse(p));
			input = input.substring(index + separator.length());
			index = input.indexOf(separator);
		}
	}

	public void write(OutputStream out, String prefix, String separator) {
		try {
			Iterator<Permission> i = values().iterator();
			out.write(prefix.getBytes());
			EscapingOutputStream eout = new EscapingOutputStream(out);
			while(i.hasNext()) {
				out.write(separator.getBytes());
				ObjectOutputStream oout = new ObjectOutputStream(eout);
				oout.writeObject(i.next());
				eout.flush();
			}
			out.write("\n".getBytes());
			out.flush();
		} catch(IOException e) {
			System.err.println("Could not write permissions to target");
			e.printStackTrace(System.err);
		}
	}
}
