package challenge24.y2014.finals.slothlers;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Aksenov239
 * Date: 03.05.14
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
public class Main {
	int port = 16700;
	int player;
	Field[][] fields;
	int len = 0;
	Visualizer visualizer;
	int turn;
	int now = 0;
	PrintWriter out;
	static HashMap<Resource, Building> producer = new HashMap<>();

	{
		producer.put(Resource.WOOD, Building.LUMBERJACK);
		producer.put(Resource.STONE, Building.QUARRY);
		producer.put(Resource.GRAIN, Building.FARM);
		producer.put(Resource.MEAT, Building.PIG_FARM);
		producer.put(Resource.FOOD, Building.TAVERN);
		producer.put(Resource.COAL, Building.COALMINE);
		producer.put(Resource.IRON, Building.IRON_MINE);
		producer.put(Resource.PRODUCT, Building.FACTORY);
		producer.put(Resource.SCORE, Building.MARKET);
	}

	public class ResourcePacket {
		Resource resource;
		int xs, ys;
		int xf, yf;

		public ResourcePacket(Resource resource, int xs, int ys, int xf, int yf) {
			this.resource = resource;
			this.xs = xs;
			this.ys = ys;
			this.xf = xf;
			this.yf = yf;
		}
	}

	public class Point {
		int x, y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode() {
			return 100 * x + y;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Point) {
				Point p = (Point) o;
				return x == p.x && y == p.y;
			}
			return false;
		}
	}

	public class Product {
		int[] produce = new int[]{0, 0, 0, 0, 2, 1, 2, 1, 1, 1, 1};
		ArrayList<ResourcePacket> flows = new ArrayList<>();
	}

	String order = "OJWSGMFCIPN";
	int[] pb = new int[order.length()];

	int[] product = {0, 0, 0, 0, 2, 1, 2, 1, 1, 1, 1};
	ArrayList<Product> products = new ArrayList<>();

	public void pushFlow(Field[][] ff, ResourcePacket flow) {
		System.out.println(ff[flow.xs][flow.ys].resources);
		ff[flow.xs][flow.ys].addResources(flow.resource.name().charAt(0) + "", -1);

		int[][] d = new int[ff.length][ff[0].length];
		for (int[] dd : d) {
			Arrays.fill(dd, -1);
		}
		Queue<Field> q = new ArrayDeque<>();
		q.add(ff[flow.xf][flow.yf]);
		d[flow.xf][flow.yf] = 0;
		while (!q.isEmpty()) {
			Field v = q.poll();
			for (Road r : v.roads.values()) {
				if (r.c == 0)
					continue;
				int xx = r.x1;
				int yy = r.y1;
				if (xx == v.x && yy == v.y) {
					xx = r.x2;
					yy = r.y2;
				}
				if (d[xx][yy] != -1)
					continue;
				q.add(ff[xx][yy]);
				d[xx][yy] = d[v.x][v.y] + 1;
			}
		}

		for (int i = 0; i < 4; i++) {
			int xx = (flow.xs + Field.dx[i] + ff.length) % ff.length;
			int yy = (flow.ys + Field.dy[i] + ff[0].length) % ff[0].length;
			if (d[xx][yy] == d[flow.xs][flow.ys] - 1 && ff[flow.xs][flow.ys].roads.get(i) != null) {
				ff[flow.xs][flow.ys].roads.get(i).c--;
				System.out.println("M " + flow.ys + " " + flow.xs + " " + flow.resource.name().charAt(0) + " " + "ESWN".charAt(i));
				out.println("M " + flow.ys + " " + flow.xs + " " + flow.resource.name().charAt(0) + " " + "ESWN".charAt(i));
				flow.xs = xx;
				flow.ys = yy;
				return;
			}
		}
	}

	public void makeTurn(int turnNumber) {
		Field[][] ff = new Field[this.fields.length][this.fields[0].length];

		HashMap<Building, ArrayList<Field>> buildings = new HashMap<>();
		HashMap<Resource, ArrayList<Field>> resources = new HashMap<>();

		for (int i = 0; i < ff.length; i++)
			for (int j = 0; j < ff[0].length; j++)
				ff[i][j] = this.fields[i][j].clone();

		for (Product p : products) {
			ArrayList<ResourcePacket> newFlow = new ArrayList<>();
			for (ResourcePacket flow : p.flows) {
				pushFlow(ff, flow);
				if (flow.xf != flow.xs || flow.yf != flow.ys)
					newFlow.add(flow);
			}
			p.flows = newFlow;
		}

		for (int i = 0; i < ff.length; i++) {
			for (int j = 0; j < ff[0].length; j++) {
				if (ff[i][j].building != null) {
					if (!buildings.containsKey(ff[i][j].building))
						buildings.put(ff[i][j].building, new ArrayList<Field>());
					buildings.get(ff[i][j].building).add(ff[i][j]);
				}

				for (Map.Entry<Resource, Integer> entry : ff[i][j].resources.entrySet()) {
					if (!resources.containsKey(entry.getKey()))
						resources.put(entry.getKey(), new ArrayList<Field>());
					for (int k = 0; k < entry.getValue(); k++) {
						resources.get(entry.getKey()).add(ff[i][j]);
					}
				}
			}
		}
		boolean[][] used = new boolean[ff.length][ff[0].length];

		for (Product p : products) {
			for (int i = 2; i < p.produce.length; i++) {
				if (buildings.get(Building.get(order.charAt(i))) == null)
					continue;
				Field field = buildings.get(Building.get(order.charAt(i))).get(0);
				// Can we produce from this field now?
				if (field.canUse()) {
					System.err.println(field.building + " " + field.resources);
					if (!used[field.x][field.y]) {
						out.println("U " + field.y + " " + field.x);
						System.out.println("U " + field.y + " " + field.x);
					}
					used[field.x][field.y] = true;
					this.fields[field.x][field.y].running = true;
					continue;
				}
				// Are we already ready for producing this?
				Field f = field.clone();
				for (ResourcePacket flow : p.flows) {
					if (flow.xf == f.x && flow.yf == f.y) {
						Resource r = flow.resource;
						if (!f.resources.containsKey(r))
							f.resources.put(r, 0);
						int cnt = f.resources.get(r);
						f.resources.put(r, cnt + 1);
					}
				}

				ArrayList<Resource> need = new ArrayList<>();
				for (Resource r : field.building.produce.keySet()) {
					if (f.resources.get(r) == null || f.resources.get(r) < field.building.produce.get(r))
						need.add(r);
				}

				System.err.println(f.building + " " + need + " " + resources.get(Resource.GRAIN));

				if (f.canUse()) {
					continue;
				}

				for (Resource r : need) {
					ArrayList<Field> toGet = resources.get(r);
					if (toGet == null)
						continue;

					for (Field from : toGet) {
						if (from.resources.get(r) == 0)
							continue;
						ResourcePacket flow = new ResourcePacket(r, from.x, from.y, f.x, f.y);
						pushFlow(ff, flow);
						p.flows.add(flow);
					}
				}
			}
		}

		out.println("T " + (turnNumber + 1));
		System.out.println("T " + (turnNumber + 1));
		out.flush();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	public void command(String command) {
		String[] s = command.split(" ");
		System.err.println(command);
		switch (s[0]) {
		case "G":
			player = Integer.parseInt(s[1]);
			return;
		case "Z":
			fields = new Field[Integer.parseInt(s[2])][Integer.parseInt(s[1])];
			return;
		case "L":
			for (int i = 0; i < fields[len].length; i++) {
				fields[len][i] = new Field(len, i, Ground.get(s[1].charAt(i)));
			}
			len++;
			if (len == fields.length) {
				visualizer = new Visualizer(fields);
				visualizer.pack();
				visualizer.setVisible(true);
				visualizer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				visualizer.setSize(fields.length * Visualizer.GROUND_SIZE, fields[0].length * Visualizer.GROUND_SIZE);
				out.println("T " + 1);
				System.out.println("T " + 1);
				makeTurn(1);
			}
			return;
		case "C":
			System.err.println("Waiting " + s[1] + " seconds");
			return;
		case "T":
			turn = Integer.parseInt(s[1]);
			now = Integer.parseInt(s[2]);
			System.err.println("Turn of player " + now);

//			if (now == player) {
//				makeTurn();
//			}
			visualizer.repaint();

			return;
		case "F":
			turn = Integer.parseInt(s[1]);
			now = Integer.parseInt(s[2]);
			int time = Integer.parseInt(s[3]);
			System.err.println(now + " Player finished turn " + turn + " with time ");
			if (now == player && turn != 0) {
				System.out.println(turn + " " + now + " " + time);
				makeTurn(turn + 1);
			}
//			visualizer.repaint();
			return;
		case "X":
			System.err.println("Error " + s[1]);
			return;
		case "P":
			int x = Integer.parseInt(s[2]);
			int y = Integer.parseInt(s[1]);
			Resource.get(s[3].charAt(0));
			fields[x][y].appear();
			System.err.println(fields[0][14].resources);
			return;
		case "W":
			x = Integer.parseInt(s[2]);
			y = Integer.parseInt(s[1]);
			Resource.get(s[3].charAt(0));
			return;
		case "S":
			//???
			return;
		case "M":
			x = Integer.parseInt(s[2]);
			y = Integer.parseInt(s[1]);
			int d = Field.getDirection(s[4]);
			fields[x][y].addResources(s[3], -1);
			fields[(x + Field.dx[d] + fields.length) % fields.length][(y + Field.dy[d] + fields[0].length) % fields[0].length]
					.addResources(s[3], 1);
			fields[x][y].paint(null);
			fields[(x + Field.dx[d] + fields.length) % fields.length][(y + Field.dy[d] + fields[0].length) % fields[0].length].paint(null);
			return;
		case "B":
			x = Integer.parseInt(s[2]);
			y = Integer.parseInt(s[1]);
			Building b = Building.get(s[3].charAt(0));
			fields[x][y].building = b;
			fields[x][y].player = now;
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					fields[(x + i + fields.length) % fields.length][(y + j + fields[0].length) % fields[0].length].player = now;
				}
			}
			return;
		case "D":
			//???
			return;
		case "R":
			x = Integer.parseInt(s[2]);
			y = Integer.parseInt(s[1]);
			d = Field.getDirection(s[3]);
			int xx = (x + Field.dx[d] + fields.length) % fields.length;
			int yy = (y + Field.dy[d] + fields[0].length) % fields[0].length;
			Road road = new Road(d, x, y, xx, yy);
			fields[x][y].roads.put(d, road);
			fields[xx][yy].roads.put((d + 2) % 4, road);
			return;
		case "E":
			x = Integer.parseInt(s[2]);
			y = Integer.parseInt(s[1]);
			d = Field.getDirection(s[3]);
			xx = (x + Field.dx[d] + fields.length) % fields.length;
			yy = (y + Field.dy[d] + fields[0].length) % fields[0].length;
			fields[x][y].roads.remove(d);
			fields[xx][yy].roads.remove((d + 2) % 4);
			return;
		case "U":
			x = Integer.parseInt(s[2]);
			y = Integer.parseInt(s[1]);
			fields[x][y].use();
			return;
		}
	}

	public void run() throws IOException {
		Socket socket = new Socket("server.ch24.org", port);
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
		out = new PrintWriter(socket.getOutputStream());

		for (int i = 0; i < 1; i++) {
			products.add(new Product());
		}

		String line = null;
		while ((line = br.readLine()) != null) {
			command(line);
		}
		socket.close();
	}

	public static void main(String[] args) throws IOException {
		new Main().run();
	}
}
