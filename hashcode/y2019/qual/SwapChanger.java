package hashcode.y2019.qual;

public class SwapChanger implements Changer {
    @Override
    public Output change(Input input, Output output) {
        while (true) {
            boolean update = false;
//            for (int i = 1; i + 2 < output.a.size(); i++) {
//                int was = input.score(output.a.get(i - 1), output.a.get(i))
//                        + input.score(output.a.get(i), output.a.get(i + 1))
//                        + input.score(output.a.get(i + 1), output.a.get(i + 2));
//                int nxt = input.score(output.a.get(i - 1), output.a.get(i + 1))
//                        + input.score(output.a.get(i + 1), output.a.get(i))
//                        + input.score(output.a.get(i), output.a.get(i + 2));
//                if (was < nxt) {
//                    update = true;
//                    System.out.println("OK!");
//                    Photo[] photos = output.a.get(i);
//                    output.a.set(i, output.a.get(i + 1));
//                    output.a.set(i + 1, photos);
//                }
//            }
            if (!update) {
                break;
            }
        }
        return output;
    }
}
