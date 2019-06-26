package hashcode.y2019.qual;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Input input = new Input(new File(args[0]), new File(args[1]));
        new VerticalMerger().merge(input);
        System.out.println(input.n);

        Map<Integer, Integer> counts = new HashMap<>();
        for (Photo photo : input.photos) {
            for (int tag : photo.tags) {
                if (!counts.containsKey(tag)) {
                    counts.put(tag, 0);
                }
                counts.put(tag, counts.get(tag) + 1);
            }
        }

        int top = 0;
        for (Integer value : counts.values()) {
            if (value >= 20) {
                top++;
            }
        }
        System.out.println(top);

        Output output = new Output();
        System.out.println(input.score(output));
    }
}
