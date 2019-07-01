package challenge24.y2014.finals.slothlers;

/**
 * Created with IntelliJ IDEA.
 * User: Aksenov239
 * Date: 03.05.14
 * Time: 12:02
 * To change this template use File | Settings | File Templates.
 */
public enum Resource {
	WOOD, STONE, GRAIN, MEAT, FOOD, COAL, IRON, PRODUCT, SCORE;

	public static Resource get(char s){
		for (Resource r: Resource.values()) {
			if (r.name().startsWith(s + ""))
				return r;
		}
		return null;
	}
}
