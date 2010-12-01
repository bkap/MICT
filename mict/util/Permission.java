package mict.util;

import java.io.*;

public class Permission implements Serializable, Comparable {
	public static Permission parse(String s) {
		int index = s.indexOf("=");
		if(index < 0) return new Permission(s);
		return new Permission(
			s.substring(0,index),
			s.substring(index+1)
		);
	}

	public Permission(String key) {
		this(key, key);
	}

	public Permission(String key, String value) {
		this.key = key;
		this.value = value;
	}

	private String key;
	private String value;

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public boolean capableOf(String action) {
		return action.startsWith(value);
	}

	public int hashCode() {
		return key.hashCode();
	}

	public int compareTo(Object other) {
		return 1;
	}

	public int compareTo(Permission other) {
		return key.compareTo(other.key);
	}

	public String toString() {
		if(key.equals(value)) return key;
		return key + '=' + value;
	}
}
