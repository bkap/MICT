package mict.server;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import javax.imageio.ImageIO;

public class DatabaseLayer {
	public DatabaseLayer(String connection, String username, String password, boolean enabled) {
		this.enabled = enabled;
		if(!enabled) return;
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(connection, username, password);
			create = con.prepareStatement("insert into chunks values (?,?,?)");
			write = con.prepareStatement("update chunks set img = ? where x = ? and y = ?");
			read = con.prepareStatement("select img from chunks where x = ? and y = ?");
			auth = con.prepareStatement("select perms from users where user = ? and passwd = ?");
			userexists = con.prepareStatement("select passwd from users where user = ?");
		} catch(ClassNotFoundException e) {
			System.err.println("Driver class init is fail:\n" + e);
			e.printStackTrace(System.err);
			System.exit(2);
		} catch(SQLException e) {
			System.err.println("SQL initial connection is fail:");
			e.printStackTrace(System.err);
			System.exit(3);
		}
	}

	private String driver = "org.postgresql.Driver";
	private Connection con = null;
	private PreparedStatement create;
	private PreparedStatement write;
	private PreparedStatement read;
	private PreparedStatement auth;
	private PreparedStatement userexists;
	private boolean enabled;

	public Chunk getChunk(int x, int y) {
		Image result = null;
		if(enabled) {
			try {
				read.setLong(1, x);
				read.setLong(2, y);
				ResultSet results = read.executeQuery();
				if(results.next()) {
					InputStream in = results.getBinaryStream("img");
					result = ImageIO.read(in);
				}
			} catch(SQLException e) {
				System.err.println("SQL is fail:");
				e.printStackTrace(System.err);
			} catch(IOException e) {
				System.err.println("IO porblems. Go die in a fire, Java:");
				e.printStackTrace(System.err);
			}
		}
		if(result == null) {
			result = new BufferedImage(Chunk.getWidth(), Chunk.getHeight(), BufferedImage.TYPE_INT_ARGB);
		}
		return new Chunk(x, y, result);
	}

	public void setChunk(Chunk c) {
		if(!enabled) return;
		try {
			System.out.println("Saving chunk " + c + " ...");
			read.setLong(1, c.getX());
			read.setLong(2, c.getY());
			ResultSet results = read.executeQuery();
			PreparedStatement ps = results.next() ? write : create;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write((BufferedImage)c.getImage(), "png", out);
			ps.setBytes(1, out.toByteArray());
			ps.setLong(2, c.getX());
			ps.setLong(3, c.getY());
			ps.executeUpdate();
			//ps.close();
			out.close();
		} catch(SQLException e) {
			System.err.println("SQL is fail. Couldn't save chunk " + c + ':');
			e.printStackTrace(System.err);
		} catch(IOException e) {
			System.err.println("IO porblems. Go die in a fire, Java:");
			e.printStackTrace(System.err);
		}
	}

	public String getPermissions(String username, String passwd) {
		String result = null;
		if(enabled) {
			try {
				userexists.setString(1, username);
				ResultSet results = userexists.executeQuery();
				if(results.next()) {
					MessageDigest md = MessageDigest.getInstance("SHA");
					InputStream in = getBinaryStream("passwd");
					in.close();
					int saltlength = 3;
					byte[] salt = new byte[saltlength];
					for(int i = 0; i < salt.length; i++) {
						salt[i] = (byte)in.read;
						md.update(salt[i]);
					}
					byte[] pbs = md.digest(passwd.getBytes());
					byte[] field = new byte[pbs.length + saltlength];
					for(int i = 0, i < saltlength; i++) field[i] = salt[i];
					for(int i = 0, j = saltlength; i < pbs.length; i++, j++) field[j] = pbs[i];
					auth.setString(1, username);
					auth.setBytes(2, field);
					results = auth.executeQuery();
					if(results.next()) {
						result = results.getString("permissions");
					}
				}
			} catch(SQLException e) {
				System.err.println("SQL is fail:");
				e.printStackTrace(System.err);
			} catch(IOException e) {
				System.err.println("IO porblems. Go die in a fire, Java:");
				e.printStackTrace(System.err);
			} catch(NoSuchAlgorithmException e) {
				System.err.println("SHA not supported. Upgrade your damn system.");
				e.printStackTrace(
			}
		} else {
			// @Ben: I'm not sure what this should return when running tests.
		}
		if(result == null) {
			result = "";
		}
		return result;
	}
}
