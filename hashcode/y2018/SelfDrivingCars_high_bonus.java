package hashcode.y2018;
import java.io.*;
import java.util.*;

public class SelfDrivingCars_high_bonus {
	int hei, wid, fleet, rides, bonus, interval;
	int[] xFrom, yFrom, xTo, yTo, start, end, length;
	
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
		
		int[] left = new int[rides];
		ArrayList<Integer>[] chain = new ArrayList[rides];
		for (int i = 0; i < rides; i++) {
			left[i] = i;
			chain[i] = new ArrayList<>();
			chain[i].add(i);
		}
		boolean[] isBorder = new boolean[rides];
		for (int mode = 0; mode < 1; mode++) {
			ArrayList<Connection> connections = new ArrayList<>();
			for (int i = 0; i < rides; i++) {
				for (int j = 0; j < rides; j++) {
					if (i == j) {
						continue;
					}
					Connection connection = new Connection(i, j);
					if (connection.dead) {
						continue;
					}
					if (connection.penalty > 13.56) {
						continue;
					}
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
		int chainCount = 0;
		for (int i = 0; i < rides; i++) {
			if (left[i] != i) {
				continue;
			}
			out.print(chain[i].size());
			for (int v : chain[i]) {
				out.print(" " + v);
			}
			out.println();
			chainCount++;
		}
		System.out.println("Chain count: " + chainCount);
	}
	
	class Connection implements Comparable<Connection> {
		int a, b;
		double penalty;
		boolean dead;

		public Connection(int a, int b) {
			this.a = a;
			this.b = b;
			int aEnd = start[a] + length[a];
			int connDist = Math.abs(xFrom[b] - xTo[a]) + Math.abs(yFrom[b] - yTo[a]);
			if (aEnd + connDist > start[b]) {
				dead = true;
				return;
			}
			penalty = (start[b] - aEnd) * 1.0 / Math.min(length[a], length[b]);
		}

		@Override
		public int compareTo(Connection that) {
			return Double.compare(penalty, that.penalty);
		}
	}
	
	static Scanner in;
	static PrintWriter out;

	public static void main(String[] args) throws IOException {
		String fileName = "e_high_bonus";
//		String fileName = "c_no_hurry";
		String inputFileName = fileName + ".in";
		String outputFileName = fileName + ".out";

		Locale.setDefault(Locale.US);
		in = new Scanner(new File(inputFileName));
		out = new PrintWriter(outputFileName);
		new SelfDrivingCars_high_bonus().run();
		in.close();
		out.close();
	}
}
