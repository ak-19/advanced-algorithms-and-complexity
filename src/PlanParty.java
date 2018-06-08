import java.io.*;
import java.util.*;


public class PlanParty {
    private static class Vertex {
        public Vertex() {
            this.weight = 0;
            this.children = new ArrayList<Integer>();
        }

        int weight;
        ArrayList<Integer> children;
    }

    private static Vertex[] ReadTree() throws IOException {
//        InputStreamReader input_stream = new InputStreamReader(new FileInputStream("files/PlanParty.txt"));
        InputStreamReader input_stream = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input_stream);
        StreamTokenizer tokenizer = new StreamTokenizer(reader);

        tokenizer.nextToken();
        int vertices_count = (int) tokenizer.nval;

        Vertex[] tree = new Vertex[vertices_count];

        for (int i = 0; i < vertices_count; ++i) {
            tree[i] = new Vertex();
            tokenizer.nextToken();
            tree[i].weight = (int) tokenizer.nval;
        }

        for (int i = 1; i < vertices_count; ++i) {
            tokenizer.nextToken();
            int from = (int) tokenizer.nval;
            tokenizer.nextToken();
            int to = (int) tokenizer.nval;
            tree[from - 1].children.add(to - 1);
            tree[to - 1].children.add(from - 1);
        }

        return tree;
    }

    private static long funFactor(Vertex[] tree, int vertex, long[] weights, boolean[] parenting) {
        if (weights[vertex] == Long.MAX_VALUE) {
            parenting[vertex] = true;
            if (tree[vertex].children.size() < 1) {
                weights[vertex] = tree[vertex].weight;
            } else {
                int subtreeRootWithGrandChildren = tree[vertex].weight;
                for (Integer child : tree[vertex].children) {
                    if (!parenting[child]) {
                        parenting[child] = true;
                        for (Integer grandChild : tree[child].children) {
                            if (!parenting[grandChild]) {
                                subtreeRootWithGrandChildren += funFactor(tree, grandChild, weights, parenting);
                            }
                        }
                        parenting[child] = false;
                    }
                }

                int onlyChildren = 0;
                for (int child : tree[vertex].children) {
                    if (!parenting[child]) {
                        parenting[child] = true;
                        onlyChildren += funFactor(tree, child, weights, parenting);
                        parenting[child] = false;
                    }
                }

                weights[vertex] = Math.max(onlyChildren, subtreeRootWithGrandChildren);
            }
        }
        parenting[vertex] = false;
        return weights[vertex];
    }

    private static long MaxWeightIndependentTreeSubset(Vertex[] tree) {
        int size = tree.length;
        if (size == 0) {
            return 0;
        }

        if (size == 1) {
            return tree[0].weight;
        }

        long[] weights = new long[size];
        boolean[] parenting = new boolean[size];
        Arrays.fill(weights, Long.MAX_VALUE);
        return funFactor(tree, 0, weights, parenting);
    }

    public static void main(String[] args) throws IOException {
        // This is to avoid stack overflow issues
        new Thread(null, new Runnable() {
            public void run() {
                try {
                    new PlanParty().run();
                } catch (IOException e) {
                }
            }
        }, "1", 1 << 26).start();
    }

    public void run() throws IOException {
        Vertex[] tree = ReadTree();
        System.out.println(MaxWeightIndependentTreeSubset(tree));
    }
}
