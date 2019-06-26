package hashcode.y2019.qual;

import java.util.HashSet;
import java.util.Set;

public class Photo {
    int id;
    boolean vert;
    int[] tags;
    boolean alive;
    Photo pair;

    boolean isGood() {
        return alive && (!vert || pair != null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return id == photo.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    void setPair(Photo pair) {
        if (!this.alive) {
            throw new RuntimeException();
        }

        this.pair = pair;
        pair.pair = this;
        pair.alive = false;

        Set<Integer> share = new HashSet<>();
        for (int tag : tags) {
            share.add(tag);
        }
        for (int tag : pair.tags) {
            share.add(tag);
        }
        tags = new int[share.size()];
        int index = 0;
        for (int tag : share) {
            tags[index++] = tag;
        }
    }

    int score(Photo next) {
        Set<Integer> setA = new HashSet<>();
        for (int tag : tags) {
            setA.add(tag);
        }

        Set<Integer> setB = new HashSet<>();
        for (int tag : next.tags) {
            setB.add(tag);
        }

        Set<Integer> share = new HashSet<>();
        share.addAll(setA);
        share.addAll(setB);

        int a = 0, b = 0, common = 0;
        for (int tag : share) {
            boolean containsA = setA.contains(tag);
            boolean containsB = setB.contains(tag);
            if (containsA && containsB) {
                common++;
            }
            if (containsA && !containsB) {
                a++;
            }
            if (!containsA && containsB) {
                b++;
            }
        }

        return Math.min(Math.min(a, b), common);
    }
}
