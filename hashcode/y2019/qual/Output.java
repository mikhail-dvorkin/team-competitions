package hashcode.y2019.qual;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @noinspection unused, WeakerAccess
 */
public class Output {
    List<Photo> a = new ArrayList<>();

    void add(Photo photo) {
        a.add(photo);
    }

    void print(PrintWriter writer) {
        writer.println(a.size());
        for (Photo photo : a) {
            if (photo.pair == null) {
                writer.println(photo.id);
            } else {
                writer.println(photo.id + " " + photo.pair.id);
            }
        }
    }
}
