package mict.networking;

import java.io.*;

public interface ProtocolSource {
	public OutputStream getOutputStream();
	public String getPrefix();
	public String getSeparator();
	public void refreshPermissions();
}
