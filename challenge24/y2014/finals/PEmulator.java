package challenge24.y2014.finals;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class PEmulator {
	public static void create() {
		int bigger = 0;
		while (true) {
			Random rnd = new Random();
			boolean[][] level = new boolean[10][10];
			boolean[][] watched = new boolean[10][10];
			int[] p = new int[]{0, 10};
			int[] d = new int[]{1, 1};
			int[] l = new int[]{60, 40};
			int turn = 0;
			while (p[0] >= 0) {
				turn++;

				int[] np = new int[]{p[0], p[1]};
				np[turn % 2] += d[turn % 2];

				if (np[0] < 0)
					break;

				if (np[turn % 2] < 0 || np[turn % 2] >= l[turn % 2]) {
					d[turn % 2] *= -1;
					continue;
				}
				int kx = (59 - np[0]) / 4;
				int ky = np[1] / 4;
				if (level.length > kx && !watched[kx][ky]) {
					watched[kx][ky] = true;
					if (turn % 2 == 1 && (rnd.nextBoolean() || rnd.nextBoolean())) {
						level[kx][ky] = true;
					}
					if (turn % 2 == 0 && d[0] == -1) {
						level[kx][ky] = true;
					}
					if (level[kx][ky])
						d[turn % 2] *= -1;
					else
						p = np;
					continue;
				}

				p = np;
			}

			if (turn > bigger) {
				bigger = turn;
				for (int i = 0; i < level.length; i++) {
					for (int j = 0; j < level.length; j++) {
						System.out.print(level[i][j] ? "1" : "0");
					}
					System.out.println();
				}

				System.out.println(turn);
			}
		}
	}

	static void run() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("P.out"));
		boolean[][] level = new boolean[10][10];
		for (int i = 0; i < level.length; i++) {
			String line = br.readLine();
			for (int j = 0; j < level[0].length; j++) {
				level[i][j] = line.charAt(j) == '1';
			}
		}
		br.close();

		int[] p = new int[]{0, 10};
		int[] d = new int[]{1, 1};
		int[] l = new int[]{60, 40};
		int turn = 0;
		while (p[0] >= 0) {
			turn++;

			int[] np = new int[]{p[0], p[1]};
			np[turn % 2] += d[turn % 2];

			if (np[0] < 0)
				break;

			if (np[turn % 2] < 0 || np[turn % 2] >= l[turn % 2]) {
				d[turn % 2] *= -1;
				continue;
			}
			int kx = (59 - np[0]) / 4;
			int ky = np[1] / 4;
			if (level.length > kx && level[kx][ky]) {
				d[turn % 2] *= -1;
				level[kx][ky] = false;
				continue;
			}

			p = np;
		}

		System.out.println(turn);
	}

	static long readLong() {
		BigInteger x = new BigInteger(in.next());
		return x.longValue();
	}

	static void writeLong(long val) {
		BigInteger x = BigInteger.valueOf(val);
		if (x.signum() < 0) {
			x = x.add(BigInteger.ONE.shiftLeft(64));
		}
		out.println(x);
	}

	static String fileName = PEmulator.class.getSimpleName().replaceFirst("_.*", "").toUpperCase();
	static MyScanner in;
	static PrintWriter out;

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		PEmulator.create();
//		new PEmulator().run();
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
