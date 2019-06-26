package hashcode.y2019.qual;

import java.io.File;

public class MainDvorkin {
    public static void main(String[] args) {
    	for (int t : new int[] {
    		1,
    		2,
    		3,
    		4,
    	}) {
    		Input input = new Input(new File("../../tests/" + t), new File("../../results"));
            new VerticalMerger().merge(input);
    		HorizontalSequencer horizontalSequencer = new HorizontalSequencer();
    		Output output = horizontalSequencer.change(input, new Output());
    		input.update(output);
			System.out.println(input.score(output));
    	}
    }
}
