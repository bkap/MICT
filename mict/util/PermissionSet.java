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
		String key = action;
		if(index >= 0) {
			key = action.substring(0,index);
		}
		Permission p = get(key);
		if(p == null) {
			p = get("root");
			return p != null;
		}
		return p.capableOf(action);
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
		setPermission(Permission.parse(input));
	}

	public void write(OutputStream out, String prefix, String separator) {
		try {
			Iterator<Permission> i = values().iterator();
			out.write(prefix.getBytes());
			while(i.hasNext()) {
				out.write(i.next().toString().getBytes());
				if(i.hasNext())
					out.write(separator.getBytes());
			}
			out.write("\n".getBytes());
			out.flush();
		} catch(IOException e) {
			System.err.println("Could not write permissions to target");
			e.printStackTrace(System.err);
		}
	}

	public String toString() {
		Iterator<Permission> i = values().iterator();
		String result = "";
		while(i.hasNext()) {
			result += i.next().toString();
			if(i.hasNext())
				result += ',';
		}
		return result;
	}
}
