package challenge24.y2014.finals.slothlers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Aksenov239
 * Date: 03.05.14
 * Time: 12:35
 * To change this template use File | Settings | File Templates.
 */
public class Field {
	static int[] dx = {0, 1, 0, -1};
	static int[] dy = {1, 0, -1, 0};
	boolean running;

	int x, y;

	Ground ground;
	Building building = null;
	HashMap<Integer, Road> roads = new HashMap<>();
	HashMap<Resource, Integer> resources = new HashMap<>();
	int remaining = 40;
	int player = -1;

	public Field(int x, int y, Ground ground) {
		this.x = x;
		this.y = y;
		this.ground = ground;
	}

	public void addResources(String s, int add) {
		for (int i = 0; i < s.length(); i++) {
			Resource r = Resource.get(s.charAt(i));
			if (!resources.containsKey(r)) {
				resources.put(r, 0);
			}
			resources.put(r, resources.get(r) + add);
		}
	}

	public static int getDirection(String road) {
		switch (road) {
		case "S":
			return 1;
		case "E":
			return 0;
		case "N":
			return 3;
		case "W":
			return 2;
		default:
			return 0;
		}
	}

	public boolean canUse() {
		if (running)
			return false;
		if (remaining == 0 && ground != Ground.GRASS)
			return false;
		if (resources.size() >= 3)
			return false;
		if (!resources.containsKey(building.product))
			resources.put(building.product, 0);

		boolean good = true;
		for (Map.Entry<Resource, Integer> r : building.produce.entrySet()) {
			if (resources.get(r.getKey()) == null) {
				return false;
			}
			good &= resources.get(r.getKey()) >= r.getValue();
		}
		return good;
	}

	public void use() {
		running = true;
		remaining--;
		if (!resources.containsKey(building.product))
			resources.put(building.product, 0);
		for (Map.Entry<Resource, Integer> r : building.produce.entrySet()) {
			System.err.println(r + " " + resources.get(r.getKey()));
			resources.put(r.getKey(), resources.get(r.getKey()) - r.getValue());
		}
	}

	public void appear() {
		running = false;
		if (resources.get(building.product) == null)
			resources.put(building.product, 0);
		resources.put(building.product, resources.get(building.product) + 1);
	}

	public void paint(Graphics g) {
		if (g == null)
			return;
		try {
			BufferedImage image = ImageIO.read(new File("ground_" + ground.toString().toLowerCase() + ".png"));
			int X = x * Visualizer.GROUND_SIZE;
			int Y = y * Visualizer.GROUND_SIZE;
			g.drawImage(image, X, Y, Visualizer.GROUND_SIZE, Visualizer.GROUND_SIZE, null);
			if (building != null) {
				image = ImageIO.read(new File("building_" + building.toString().toLowerCase().replace("_", "") + ".png"));
				g.drawImage(image, X + (Visualizer.GROUND_SIZE - Visualizer.BUILDING_SIZE) / 2, Y + (Visualizer.GROUND_SIZE - Visualizer.BUILDING_SIZE) / 2, Visualizer.BUILDING_SIZE, Visualizer.BUILDING_SIZE, null);
			}
			g.setColor(Color.CYAN);

			for (int d : roads.keySet())
				g.drawLine(X + Visualizer.GROUND_SIZE / 2, Y + Visualizer.GROUND_SIZE / 2,
						X + dx[d] * Visualizer.GROUND_SIZE + Visualizer.GROUND_SIZE / 2,
						Y + dy[d] * Visualizer.GROUND_SIZE + Visualizer.GROUND_SIZE / 2);

			int total = 0;
			for (Map.Entry<Resource, Integer> r : resources.entrySet()) {
				for (int i = 0; i < r.getValue(); i++) {
					int xx = (total % 2) * (Visualizer.GROUND_SIZE - Visualizer.RESOURCE_SIZE) + x * Visualizer.GROUND_SIZE;
					int yy = (total >> 1) * (Visualizer.GROUND_SIZE - Visualizer.RESOURCE_SIZE) + y * Visualizer.GROUND_SIZE;
					image = ImageIO.read(new File("res_" + r.getKey().toString().toLowerCase() + ".png"));
					g.drawImage(image, xx, yy, Visualizer.RESOURCE_SIZE, Visualizer.RESOURCE_SIZE, null);
					total++;
				}
			}
		} catch (IOException e) {
			System.err.println("building_" + building.toString().toLowerCase().replace("_", "") + ".png");
			e.printStackTrace();
		}
	}

	@Override
	public Field clone() {
		Field field = new Field(x, y, ground);
		field.building = building;
		field.remaining = remaining;
		field.resources = new HashMap<>();
		for (Map.Entry<Resource, Integer> entry : resources.entrySet()) {
			field.resources.put(entry.getKey(), entry.getValue());
		}
		field.roads = roads;
		for (Road r : roads.values()) {
			r.c = 1;
		}
		field.running = running;

		field.player = player;
		return field;
	}

}
