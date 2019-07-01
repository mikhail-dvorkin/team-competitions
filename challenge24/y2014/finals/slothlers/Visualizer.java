package challenge24.y2014.finals.slothlers;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Aksenov239
 * Date: 03.05.14
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("serial")
public class Visualizer extends JFrame {
	static int GROUND_SIZE = 30;
	static int BUILDING_SIZE = 20;
	static int RESOURCE_SIZE = 10;
	Field[][] fields;

	public Visualizer(Field[][] fields) {
		super("visualizer");
		this.fields = fields;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		for (int i = 0; i < fields.length; i++) {
			for (int j = 0; j < fields[i].length; j++) {
				fields[i][j].paint(g);
			}
		}
	}
}
