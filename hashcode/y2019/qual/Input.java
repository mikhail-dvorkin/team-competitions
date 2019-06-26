package hashcode.y2019.qual;

import java.io.*;
import java.util.*;

/**
 * @noinspection unused, WeakerAccess
 */
public class Input {
    public static Input INSTANCE;

    File file;
    File outputDir;

    int n;
    int tagCount;
    Photo[] photos;

    public Input(File file, File outputDir) {
        this.file = file;
        this.outputDir = outputDir;
        read();
        INSTANCE = this;
    }

    void prepare() {
        int goodCount = 0;
        for (Photo photo : photos) {
            if (photo.isGood()) {
                goodCount++;
            }
        }
        Photo[] goodPhotos = new Photo[goodCount];
        int index = 0;
        for (Photo photo : photos) {
            if (photo.isGood()) {
                goodPhotos[index++] = photo;
            }
        }
        photos = goodPhotos;
        n = goodCount;
    }

    private void read() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            n = Integer.parseInt(reader.readLine());
            photos = new Photo[n];
            Map<String, Integer> tags = new HashMap<>();
            for (int i = 0; i < n; i++) {
                String[] tokens = reader.readLine().split("\\s+");
                boolean vert = tokens[0].equals("V");
                int m = Integer.valueOf(tokens[1]);
                int[] t = new int[m];
                for (int j = 2; j <= m + 1; j++) {
                    String tagString = tokens[j];
                    int tag;
                    if (tags.containsKey(tagString)) {
                        tag = tags.get(tagString);
                    } else {
                        tag = tags.size();
                        tags.put(tagString, tag);
                    }
                    t[j - 2] = tag;
                }
                photos[i] = new Photo();
                photos[i].id = i;
                photos[i].tags = t;
                photos[i].vert = vert;
                photos[i].alive = true;
            }
            tagCount = tags.size();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    int score(Output output) {
        int result = 0;

        int m = output.a.size();
        for (int i = 0; i + 1 < m; i++) {
            result += output.a.get(i).score(output.a.get(i + 1));
        }

        return result;
    }

    void update(Output output) {
        int score = score(output);
        File scoreFile = new File(outputDir, file.getName() + ".score");
        File outputFile = new File(outputDir, file.getName() + ".out");

        boolean update;
        if (!scoreFile.isFile() || !outputFile.isFile()) {
            update = true;
        } else {
            while (true) {
                try (BufferedReader reader = new BufferedReader(new FileReader(scoreFile))) {
                    int pScore = Integer.parseInt(reader.readLine());
                    update = pScore < score;
                    break;
                } catch (Exception ignored) {
                    sleep();
                }
            }
        }

        if (update) {
            while (true) {
                try {
                    try (PrintWriter writer = new PrintWriter(outputFile)) {
                        output.print(writer);
                    }

                    try (PrintWriter writer = new PrintWriter(scoreFile)) {
                        writer.println(score);
                    }

                    break;
                } catch (Exception ignored) {
                    sleep();
                }
            }

            System.out.println("[" + new Date() + "] New output for '" + file + "': new score is " + score + ".");
        }
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
            // No operations.
        }
    }
}
