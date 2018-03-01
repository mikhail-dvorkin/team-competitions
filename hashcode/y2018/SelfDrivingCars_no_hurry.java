package hashcode.y2018;
import java.io.*;
import java.util.*;

public class SelfDrivingCars_no_hurry {
	int hei, wid, fleet, rides, bonus, interval;
	int[] xFrom, yFrom, xTo, yTo, start, end, length;
	static final int CONNECTION_THRESHOLD = 150;
	
	public void run() {
		hei = in.nextInt();
		wid = in.nextInt();
		fleet = in.nextInt();
		rides = in.nextInt();
		bonus = in.nextInt();
		interval = in.nextInt();
		xFrom = new int[rides];
		yFrom = new int[rides];
		xTo = new int[rides];
		yTo = new int[rides];
		start = new int[rides];
		end = new int[rides];
		length = new int[rides];
		System.out.println("Total car-hours:\t" + fleet * interval);
		int desiredTravel = 0;
		for (int i = 0; i < rides; i++) {
			xFrom[i] = in.nextInt();
			yFrom[i] = in.nextInt();
			xTo[i] = in.nextInt();
			yTo[i] = in.nextInt();
			start[i] = in.nextInt();
			end[i] = in.nextInt();
			length[i] = Math.abs(xFrom[i] - xTo[i]) + Math.abs(yFrom[i] - yTo[i]);
			desiredTravel += length[i];
		}
		System.out.println("Desired travel: \t" + desiredTravel);
		
		/*
		ArrayList<Integer>[][] startsHere = new ArrayList[hei][wid];
		ArrayList<Integer>[][] endsHere = new ArrayList[hei][wid];
		for (int i = 0; i < hei; i++) {
			for (int j = 0; j < wid; j++) {
				startsHere[i][j] = new ArrayList<>();
				endsHere[i][j] = new ArrayList<>();
			}
		}
		for (int i = 0; i < rides; i++) {
			startsHere[xFrom[i]][yFrom[i]].add(i);
			endsHere[xTo[i]][yTo[i]].add(i);
		}
		*/
		int[] left = new int[rides];
		ArrayList<Integer>[] chain = new ArrayList[rides];
		int[] chainLength = new int[rides];
		for (int i = 0; i < rides; i++) {
			left[i] = i;
			chain[i] = new ArrayList<>();
			chain[i].add(i);
			chainLength[i] = length[i];
		}
		boolean[] isBorder = new boolean[rides];
		for (int mode = 0; mode < 2; mode++) {
			ArrayList<Connection> connections = new ArrayList<>();
			for (int i = 0; i < rides; i++) {
				for (int j = 0; j < rides; j++) {
					if (i == j) {
						continue;
					}
					int connDist = Math.abs(xFrom[j] - xTo[i]) + Math.abs(yFrom[j] - yTo[i]);
					if (mode == 0 && connDist > CONNECTION_THRESHOLD) {
						continue;
					}
					if (mode == 1 && (!isBorder[i] || !isBorder[j])) {
						continue;
					}
					Connection connection = new Connection(i, j);
					connections.add(connection);
				}
			}
			System.out.println("Mode " + mode + ". " + connections.size() + " connections");
			Collections.sort(connections);
			for (Connection connection : connections) {
				int a = connection.a;
				int b = connection.b;
				int la = left[a];
				if (la == left[b] || b != left[b]) {
					continue;
				}
				if (chain[la].get(chain[la].size() - 1) != a) {
					continue;
				}
				for (int c : chain[b]) {
					left[c] = la;
				}
				chain[la].addAll(chain[b]);
				chainLength[la] += chainLength[b] + connection.connDist;
			}
			if (mode == 0) {
				for (int i = 0; i < rides; i++) {
					if (left[i] != i) {
						continue;
					}
					isBorder[i] = true;
					isBorder[chain[i].get(chain[i].size() - 1)] = true;
				}
			}
			System.out.println("Mode done.");
		}
		ArrayList<Integer> order = null;
		for (int i = 0; i < rides; i++) {
			if (left[i] != i) {
				continue;
			}
			System.out.println(i + " " + chain[i].size() + " " + chainLength[i]);
			order = chain[i];
		}
		if (order.size() != rides) {
			throw new AssertionError();
		}
		int[][] d = new int[rides + 1][fleet + 1];
		int[][] how = new int[rides + 1][fleet + 1];
		for (int i = 0; i < d.length; i++) {
			Arrays.fill(d[i], -1);
		}
		d[0][0] = 0;
		for (int i = 0; i < rides; i++) {
			int a = order.get(i);
			int len = length[a];
			int win = len;
			len += Math.abs(xFrom[a]) + Math.abs(yFrom[a]);
			int j = i + 1;
			while (j < rides) {
				a = order.get(j - 1);
				int b = order.get(j);
				int conn = Math.abs(xFrom[b] - xTo[a]) + Math.abs(yFrom[b] - yTo[a]);
				if (len + conn + length[b] <= interval) {
					len += conn + length[b];
					win += length[b];
					j++;
					continue;
				}
				break;
			}
			for (int k = 0; k <= fleet; k++) {
				if (d[i][k] == -1) {
					continue;
				}
				if (k < fleet && d[j][k + 1] < d[i][k] + win) {
					d[j][k + 1] = d[i][k] + win;
					how[j][k + 1] = i;
				}
				if (d[i + 1][k] < d[i][k]) {
					d[i + 1][k] = d[i][k];
					how[i + 1][k] = -1;
				}
			}
		}
		System.out.println("Ans: " + d[rides][fleet]);
		int k = fleet;
		int i = rides;
		while (k > 0) {
			int j = how[i][k];
			if (j == -1) {
				i--;
				continue;
			}
			out.print(i - j);
			for (int t = j; t < i; t++) {
				out.print(" " + order.get(t));
			}
			out.println();
			i = j;
			k--;
		}
	}
	
	class Connection implements Comparable<Connection> {
		int a, b, connDist;
		double penalty;

		public Connection(int a, int b) {
			this.a = a;
			this.b = b;
			connDist = Math.abs(xFrom[b] - xTo[a]) + Math.abs(yFrom[b] - yTo[a]);
			penalty = connDist * 1.0 / Math.min(length[a], length[b]);
		}

		@Override
		public int compareTo(Connection that) {
			return Double.compare(penalty, that.penalty);
		}
	}
	
	static Scanner in;
	static PrintWriter out;

	public static void main(String[] args) throws IOException {
		String fileName = "c_no_hurry";
		String inputFileName = fileName + ".in";
		String outputFileName = fileName + ".out";

		Locale.setDefault(Locale.US);
		in = new Scanner(new File(inputFileName));
		out = new PrintWriter(outputFileName);
		new SelfDrivingCars_no_hurry().run();
		in.close();
		out.close();
	}
}
