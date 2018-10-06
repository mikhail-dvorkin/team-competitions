import java.io.*;
import java.net.*;
import java.util.*;

public class R {
	static void connect() throws MalformedURLException, IOException, ProtocolException {
		URL url = new URL("https://ipsc.ksp.sk/2018/practice/problems/r1");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Cookie", "ipscsessid=aaf5762478c617d4ea778e1cb622350bdf474520");
		connection.connect();
		System.out.println(connection.getResponseCode());
		System.out.println(connection.getResponseMessage());
		Scanner in = new Scanner(connection.getInputStream());
		while (in.hasNextLine()) {
			System.out.println(in.nextLine());
		}
		in.close();
	}

	public static void main(String[] args) throws IOException {
		connect();
	}
}
