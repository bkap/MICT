package mict.util;

import java.io.*;
import java.util.*;

public abstract class ConfigParser {
	public static String[] expand(String[] args) {
		LinkedList<String> xargs = new LinkedList<String>();
		for(int i = 0; i < args.length; i++) {
			xargs.add(args[i]);
		}
		ListIterator<String> i = xargs.listIterator(0);
		while(i.hasNext()) {
			String s = i.next();
			while(s.startsWith("-")) s = s.substring(1);
			if(s.equals("")) {
				i.remove();
				continue;
			}
			if(s.startsWith("config=")) {
				i.remove();
				String file = s.substring("config=".length());
				expand(i, file);
			}
		}
		args = new String[xargs.size()];
		int j = 0;
		for(i = xargs.listIterator(); i.hasNext(); j++) {
			String s = i.next();
			args[j] = s;
		}
		return args;
	}

	protected static void expand(ListIterator<String> i, String file) {
		LinkedList<String> yargs = new LinkedList<String>();
		try {
			FileInputStream fin = new FileInputStream(file);
			String line = "";
			boolean reading = true;
			while(reading) {
				int read = fin.read();
				if(read < 0) {
					reading = false;
					read = '\n';
				} else {
					line += (char)read;
				}
				if(read == '\n' && !line.equals("")) {
					int index = line.indexOf('=');
					if(index < 0) yargs.add(line);
					else {
						yargs.add(line.substring(0,index).trim());
						yargs.add(line.substring(index+1).trim());
					}
					line = "";
				}
			}
			fin.close();
		} catch(IOException e) {
			System.err.println("Could not read configuration file " + file + ". Contents ignored.");
		}
		ListIterator<String> j = yargs.listIterator(0);
		while(j.hasNext()) {
			String s = j.next();
			String t = s;
			while(s.startsWith("-")) s = s.substring(1);
			if(s.equals("")) {
				continue;
			}
			if(s.equals("config")) {
				j.remove();
				if(j.hasNext()) {
					String file2 = j.next();
					j.remove();
					expand(i, file2);
				} else {
					System.err.println("In file " + file + ", expected filename after config= directive. Ignoring.");
				}
			} else {
				i.add(t);
			}
		}
	}

	public static boolean is(String s) {
		s = s.toLowerCase().trim();
		return
			"yes".startsWith(s) ||
			"true".startsWith(s) ||
			"allowed".startsWith(s) ||
			"okay".startsWith(s) ||
			"1".equals(s);
	}
}
