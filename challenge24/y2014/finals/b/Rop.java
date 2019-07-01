package challenge24.y2014.finals.b;

import java.util.List;

/**
 * Created by ab on 5/3/14.
 */
public class Rop implements Op {

	public final int index;
	public String id;

	public Rop(int index) {
		this.index = index;
	}

	@Override
	public Type type() {
		return Type.R;
	}

	@Override
	public String toFunnyString(@SuppressWarnings("unused") String ident) {
		return "" + id;
	}

	@Override
	public void makeReferences(List<String> ids, @SuppressWarnings("unused") int[] holder) {
		id = ids.get(ids.size() - 1 - index);
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public String toString() {
		return Integer.toString(index);
	}
}
