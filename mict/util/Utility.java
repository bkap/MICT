package mict.util;

import java.util.*;

public abstract class Utility {
	public static byte[] toByteArray(LinkedList<Byte> list) {
		byte[] result = new byte[list.size()];
		for(int i = 0; i < result.length; i++) {
			result[i] = list.poll().byteValue();
		}
		return result;
	}
}
