package challenge24.y2014.finals.b;

import java.util.List;

/**
 * Created by ab on 5/3/14.
 */
interface Op {
	public Type type();

	public String toFunnyString(String ident);

	public final String SHIFT = " ";

	void makeReferences(List<String> ids, int[] holder);

	int size();
}
