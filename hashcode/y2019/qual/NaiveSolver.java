package hashcode.y2019.qual;

public class NaiveSolver implements Solver {
    @Override
    public Output solve(Input input) {
        Output output = new Output();

        for (int i = 0; i < input.n; i++) {
            output.add(input.photos[i]);
        }

        return output;
    }
}
