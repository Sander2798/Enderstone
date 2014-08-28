package org.enderstone.server.uuid;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.json.JSONObject;

public class ServerRequest {

	private final String connectURL;

	public ServerRequest(String connectURL) {
		this.connectURL = connectURL;
	}

	public JSONObject get() throws IOException {
		return parseDataFromURL(connectURL);
	}

	public static JSONObject parseDataFromURL(String connect) throws IOException {
		URL url = new URL(connect);
		URLConnection uc = url.openConnection();
		try (Scanner scanner = new Scanner(uc.getInputStream())) {
			scanner.useDelimiter("\\A");
			return new JSONObject(scanner.next());
		}
	}
}