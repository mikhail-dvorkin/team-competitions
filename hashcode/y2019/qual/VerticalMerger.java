package hashcode.y2019.qual;

import java.util.*;

public class VerticalMerger {
    void merge(Input input) {
        List<Photo> v = new ArrayList<>();
        for (Photo photo : input.photos) {
            if (photo.vert) {
                v.add(photo);
            }
        }
        v.sort(Comparator.comparingInt(o -> o.tags.length));

        for (int i = 0; i < v.size() / 2; i++) {
            v.get(i).setPair(v.get(v.size() - 1 - i));
        }

        input.prepare();
    }
}
