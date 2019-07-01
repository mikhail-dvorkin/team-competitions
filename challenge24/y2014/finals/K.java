package challenge24.y2014.finals;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.net.*;

public class K {
	double da = 0.01;
	BufferedReader in;

	public class Point implements Comparable<Point> {
		int id;
		double latitude, longitude;

		public Point(int id, double latitude, double longitude) {
			this.id = id;
			this.latitude = latitude;
			this.longitude = longitude;
		}

		@Override
		public int compareTo(Point p) {
			return Double.compare(latitude, p.latitude);
		}
	}

	public static void prepare() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("input_nodes.txt"));
		PrintWriter[] writes = new PrintWriter[360];
		for (int i = 0; i < writes.length; i++) {
			writes[i] = new PrintWriter("nodes/longitude" + i);
		}

		String line = null;
		while ((line = br.readLine()) != null) {
			String[] s = line.split(" ");
			writes[(int) (Double.parseDouble(s[1]) + 180)].println(line);
		}
		for (PrintWriter writer : writes) {
			writer.close();
		}
		br.close();
	}

	public static boolean inEpsilon(double l1, double l2, double eps) {
		return Math.abs(l1 - l2) < eps || Math.abs(l1 - l2 - 360) < eps ||
				Math.abs(l1 - l2 + 360) < eps;
	}

	public int readNear(double longitude) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("nodes/longitude" + (int)longitude));
		String line = null;
		int best = 0;
		double ba = Double.MAX_VALUE;
		while ((line = br.readLine()) != null) {
			String[] s = line.split(" ");
			double ll = Double.parseDouble(s[1]);
			double latitude = Double.parseDouble(s[2]);

			if (inEpsilon(latitude, ll, da / 2) && Math.abs(latitude) < ba) {
				ba = Math.abs(latitude);
				best = Integer.parseInt(s[0]);
			}
		}
		br.close();
		return best;
	}

	public boolean ask(int n1, int n2) throws IOException{
		out.println("closer " + n1 + " " + n2);
		out.flush();
		String result = in.readLine();
		System.err.println("Query result: " + result);
		return true;
	}

	public double latitude() throws IOException{
		double l = -180;
		double r = 180;
		while (r - l > da) {
			double m1 = (l + r) / 2 - da / 2;
			double m2 = (l + r) / 2 + da / 2;
			if (m1 < -180)
				m1 += 360;
			if (m2 > 180)
				m2 -= 360;

			int n1 = readNear(m1);
			int n2 = readNear(m2);
			if (ask(n1, n2)) {
				r = Math.min((l + r) / 2 + da / 2, 180);
			} else {
				l = Math.max((l + r) / 2 - da / 2, -180);
			}
		}
		return (l + r) / 2;
	}

	void run() throws IOException {
		prepare();
		Socket socket = new Socket("server.ch24.org", 0000);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
		out = new PrintWriter(socket.getOutputStream());

		while (true) {
			String command = in.readLine();
			if (command == null) {
				break;
			}
			System.err.println(command);
		}
		socket.close();
	}

	static long readLong() {
		BigInteger x = new BigInteger(myIn.next());
		return x.longValue();
	}

	static void writeLong(long val) {
		BigInteger x = BigInteger.valueOf(val);
		if (x.signum() < 0) {
			x = x.add(BigInteger.ONE.shiftLeft(64));
		}
		out.println(x);
	}

	static String fileName = K.class.getSimpleName().replaceFirst("_.*", "").toUpperCase();
	static MyScanner myIn;
	static PrintWriter out;

	public static void main(String[] args) throws IOException {
		Locale.setDefault(Locale.US);
		new K().run();
	}

	static class MyScanner {
		BufferedReader br;
		StringTokenizer st;

		MyScanner(BufferedReader br) {
			this.br = br;
		}

		void findToken() {
			while (st == null || !st.hasMoreTokens()) {
				try {
					st = new StringTokenizer(br.readLine());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

		String next() {
			findToken();
			return st.nextToken();
		}

		int nextInt() {
			return Integer.parseInt(next());
		}

		long nextLong() {
			return Long.parseLong(next());
		}

		double nextDouble() {
			return Double.parseDouble(next());
		}
	}
}
