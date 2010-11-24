package mict.networking;

import java.io.*;

public class Permission implements Serializable, Comparable {
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
}
