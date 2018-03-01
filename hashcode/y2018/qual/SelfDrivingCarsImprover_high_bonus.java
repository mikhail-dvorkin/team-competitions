package hashcode.y2018.qual;
import java.io.*;
import java.util.*;

public class SelfDrivingCarsImprover_high_bonus {
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
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] paths = new ArrayList[fleet];
		boolean[][] bonuses = new boolean[fleet][];
		for (int i = 0; i < fleet; i++) {
			int count = ans.nextInt();
			paths[i] = new ArrayList<>();
			for (int j = 0; j < count; j++) {
				paths[i].add(ans.nextInt());
			}
			bonuses[i] = bonuses(paths[i]);
			System.out.println(Arrays.toString(bonuses[i]));
		}
		main:
		for (;;) {
			for (int r = 0; r < fleet; r++) {
				for (int x = 0; x < bonuses[r].length; x++) {
					if (bonuses[r][x]) {
						continue;
					}
					System.out.println("Trying " + r + " " + x);
					int a = paths[r].get(x);
					for (int s = fleet - 5; s < fleet; s++) {
						if (s == r) {
							continue;
						}
						int winBefore = win(bonuses[s]);
						@SuppressWarnings("unchecked")
						ArrayList<Integer> newPath = (ArrayList<Integer>) paths[s].clone();
						int i = 0;
						while (i < newPath.size() && start[newPath.get(i)] < start[a]) {
							i++;
						}
						newPath.add(i, a);
						boolean[] newBonuses = bonuses(newPath);
						if (newBonuses == null) {
							continue;
						}
						int winAfter = win(newBonuses);
						boolean improve = winAfter > winBefore;
						if (improve) {
							paths[s] = newPath;
							paths[r].remove(x);
							bonuses[s] = bonuses(paths[s]);
							bonuses[r] = bonuses(paths[r]);
							System.out.println(r + " → " + s);
							continue main;
						}
					}
				}
			}
			break;
		}
		for (int i = 0; i < fleet; i++) {
			out.print(paths[i].size());
			for (int v : paths[i]) {
				out.print(" " + v);
			}
			out.println();
		}
	}

	boolean[] bonuses(ArrayList<Integer> path) throws AssertionError {
		boolean[] res = new boolean[path.size()];
		int x = 0;
		int y = 0;
		int time = 0;
		for (int i = 0; i < res.length; i++) {
			int a = path.get(i);
			time += Math.abs(xFrom[a] - x) + Math.abs(yFrom[a] - y);
			if (time <= start[a]) {
				res[i] = true;
			} else {
				if (Math.abs(xFrom[a]) + Math.abs(yFrom[a]) <= start[a]) {
					System.out.println("Improvable");
				}
			}
			time = Math.max(time, start[a]);
			time += length[a];
			if (time > end[a]) {
				return null;
			}
			x = xTo[a];
			y = yTo[a];
		}
		return res;
	}
	
	int win(boolean[] bonuses) {
		int res = 0;
		for (boolean b : bonuses) {
			if (b) {
				res++;
			}
		}
		return res;
	}
	
	static Scanner in;
	static Scanner ans;
	static PrintWriter out;

	public static void main(String[] args) throws IOException {
		String fileName = "e_high_bonus";
		String inputFileName = fileName + ".in";
		String answerFileName = fileName + ".out";
		String outputFileName = fileName + ".out2";

		Locale.setDefault(Locale.US);
		in = new Scanner(new File(inputFileName));
		ans = new Scanner(new File(answerFileName));
		out = new PrintWriter(outputFileName);
		new SelfDrivingCarsImprover_high_bonus().run();
		in.close();
		ans.close();
		out.close();
	}
}
