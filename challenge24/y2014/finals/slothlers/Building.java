package challenge24.y2014.finals.slothlers;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Aksenov239
 * Date: 03.05.14
 * Time: 12:06
 * To change this template use File | Settings | File Templates.
 */
public enum Building {
	FOUNDATION("O", "WS", null, "", 0, null),
	JUNCTION("J", "WS", null, "", 0, null),
	LUMBERJACK("W", "SSSS", null, "", 4, Resource.WOOD),
	QUARRY("S", "WWWW", Ground.STONE, "", 2, Resource.STONE),
	FARM("G", "WWWWSS", Ground.GRASS, "", 3, Resource.GRAIN),
	PIG_FARM("M", "WWSSSS", null, "G", 5, Resource.MEAT),
	TAVERN("F", "WWWWSSSS", null, "GM", 2, Resource.FOOD),
	COALMINE("C", "WWWWSSSSSS", Ground.COAL, "F", 3, Resource.COAL),
	IRON_MINE("I", "WWWWSSSSSS", Ground.IRON, "F", 3, Resource.IRON),
	FACTORY("P", "WWSSSSSSII", null, "IC", 2, Resource.PRODUCT),
	MARKET("N", "WWWWSSIIII", null, "P", 1, Resource.SCORE);

	public String type;
	public HashMap<Resource, Integer> build;
	public Ground ground;
	public HashMap<Resource, Integer> produce;
	int time;
	Resource product;
	int zero;

	private Building(String type, String toBuild, Ground ground, String toProduce, int time, Resource product) {
		this.type = type;
		this.ground = ground;
		this.time = time;
		this.product = product;

		build = new HashMap<>();
		for (int i = 0; i < toBuild.length(); i++) {
			Resource r = Resource.get(toBuild.charAt(i));
			if (!build.containsKey(r))
				build.put(r, 0);
			int cnt = build.get(r);
			build.put(r, cnt + 1);
		}

		produce = new HashMap<>();
		for (int i = 0; i < toProduce.length(); i++) {
			Resource r = Resource.get(toProduce.charAt(i));
			if (!produce.containsKey(r))
				produce.put(r, 0);
			int cnt = produce.get(r);
			produce.put(r, cnt + 1);
		}
	}

	public static Building get(char building) {
		for (Building b : Building.values())
			if (b.type.equals("" + building))
				return b;
		return null;
	}
}
