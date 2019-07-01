package challenge24.y2014.finals.slothlers;

/**
 * Created with IntelliJ IDEA.
 * User: Aksenov239
 * Date: 03.05.14
 * Time: 11:58
 * To change this template use File | Settings | File Templates.
 */
public enum Ground {
	GRASS, WATER, STONE, COAL, IRON;

	public static Ground get(char resource) {
		switch (resource) {
		case 'G':
			return GRASS;
		case 'W':
			return WATER;
		case 'S':
			return STONE;
		case 'C':
			return COAL;
		case 'I':
			return IRON;
		default:
			return null;
		}
	}
}
