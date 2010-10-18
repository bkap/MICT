package mict.server;

import javax.net.ssl.*;
import java.io.*;

public class Waiter extends Thread {
	public Waiter(SSLSocket patron, Server parent) throws IOException {
		this.patron = patron;
		this.parent = parent;
		out = new PrintWriter(new OutputStreamWriter(patron.getOutputStream()));
		in = new BufferedReader(new InputStreamReader(patron.getInputStream()));
		setDaemon(true);
	}

	private SSLSocket patron;
	private Server parent;
	private PrintWriter out;
	private BufferedReader in;
	private long x = 0;
	private long y = 0;
	private long w = 1024;
	private long h = 1024;
	private PermissionSet perms;
	private Vector<HistoryLayer> history = new Vector<HistoryLayer>();

	public void run() {
		// DO WORK, SON
		String username = "";
		String password = "";
		while(true) {
			int read = in.read();
			if(read == -1) {
				close();
				return;
			} else if(read == '\n') {
				int index = buffer.indexOf(' ');
				if(index > 0) {
					password = username.substring(index + 1);
					username = username.substring(0, index);
				}
				break;
			} else {
				username += (char)read;
			}
		}
		perms = authenticate(username, password);
		if(!perms.acceptConnection()) {
			close();
			return;
		}

		// send tool set
		// set prior x,y
		String buffer = "";
		String action = "";
		while(true) {
			int read = in.read();
			if(read == -1) break;
			if(read == ' ') {
				if(action == "") action = buffer;
				else dispatch(action, buffer);
				buffer = "";
			} else if(read == '\n') {
				dispatch(action, buffer);
				buffer = "";
				action = "";
			} else {
				buffer += (char)read;
			}
		}
		close();
	}

	private void dispatch(String action, String phrase) {
		if(action.startsWith('.')) { // it's a tool
			history.add(parent.getCanvas().draw(x, y, action.substring(1), phrase, this));
		} else { // it's not a tool
			// TODO fill this out later
		}
	}

	/** checks to see if the given four-member long[] intersects with the user's viewing area
	 */
	public boolean intersects(long[] rect) {
		return
			!(rect[0] + rect[2] < x || rect[0] > x + w) &&
			!(rect[1] + rect[3] < y || rect[1] > y + h);
	}

	protected void move(long x, long y) {
		this.x = x;
		this.y = y;
	}

	public void sendCanvasRectangle(long x, long y, long width, long height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_ARGB);
		int[] area = Chunk.getAffectedChunks(x, y, width, height);
		Graphics g = img.getGraphics();
		for(int i = area[0]; i < area[2]; i++) {
			for(int j = area[1]; j < area[3]; j++) {
				Image tile = getChunk(i, j).getImage();
				g.drawImage(
					tile,
					i * Chunk.getWidth() - x,
					j * Chunk.getHeight() - y,
					null
				);
			}
		}
		// do I have to wait? probably. ick.
		out.print("imgrect" + x + '.' + y + " ");
		ImageIO.write(img, "png", new EscapingOutputStream(out));
		out.println();
	}

	public void sendMoveOrder(long x, long y) {
		
	}

	public void sendToolSet() {
		
	}

	public void sendCanvasChange(long x, long y, String tool, String data) {
		out.println('.' + tool + '@' + x + ',' + y + ' ' + data);
	}

	protected void send(String type, String data) {
		//out.println('[' + type + " " + escape(data) + ']');
		//out.flush();
		out.println(type + ' ' + data);
	}

	/*public String escape(String s) {
		String result = "";
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if(c == '\\' || c == '[' || c == ']')
				result += '\\';
			result += c;
		}
		return result;
	}*/

	protected void close() {
		parent.clients.remove(this);
		try {
			patron.close();
			in.close();
			out.close();
		} catch(IOException e) {
			// Nothing to see here, move along.
		}
	}
}
