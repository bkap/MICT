package mict.util;

import java.io.*;
import java.util.*;

public abstract class Utility {
	public static byte[] toByteArray(LinkedList<Byte> list) {
		byte[] result = new byte[list.size()];
		for(int i = 0; i < result.length; i++) {
			result[i] = list.poll().byteValue();
		}
		return result;
	}

	public static String[] expand(String[] args) {
		LinkedList<String> xargs = new LinkedList<String>();
		for(int i = 0; i < args.length; i++) {
			xargs.add(args[i]);
		}
		for(ListIterator<String> i = xargs.listIterator(0); i.hasNext();) {
			String s = i.next();
			while(s.startsWith("-")) s = s.substring(1);
			if(s.equals("")) {
				i.remove();
				continue;
			}
			if(s.startsWith("config=")) {
				xargs.remove(i);
				String file = s.substring("config=".length());
				try {
					FileInputStream fin = new FileInputStream(file);
					String line = "";
					while(true) {
						int read = fin.read();
						if(read < 0) break;
						if(read == '\n' && !line.equals("")) {
							i.add(line);
							line = "";
						}
					}
					fin.close();
					if(!line.equals("")) i.add(line);
				} catch(IOException e) {
					System.err.println("Could not read configuration file " + file + ". Contents ignored.");
				}
			}
		}
		args = new String[xargs.size()];
		int j = 0;
		for(ListIterator<String> i = xargs.listIterator(); i.hasNext(); j++) {
			String s = i.next();
			args[j] = s;
		}
		return args;
	}
}
