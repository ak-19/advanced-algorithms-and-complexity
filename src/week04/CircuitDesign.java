package week04;

import java.io.*;
import java.util.*;

public class CircuitDesign {
    private static class Clause {
        int firstVar;
        int secondVar;
    }

    public static class TwoSatisfiability {
        int numVars;
        Clause[] clauses;
        int postOrderCounter;
        public TwoSatisfiability(int n, int m) {
            numVars = n;
            clauses = new Clause[m];
            for (int i = 0; i < m; ++i) {
                clauses[i] = new Clause();
            }
        }

        public boolean isSatisfiable(int[] result) {
            ArrayList<HashSet<Integer>> components = new ArrayList<HashSet<Integer>>();
            ArrayList<Integer>[] g = (ArrayList<Integer>[]) new ArrayList[2 * numVars];
            ArrayList<Integer>[] gR = (ArrayList<Integer>[]) new ArrayList[2 * numVars];
            for (int i = 0; i < 2 * numVars; i++) {
                g[i] = new ArrayList<Integer>();
                gR[i] = new ArrayList<Integer>();
            }

            buildImplicationGraph(g, gR);

            boolean visited[] = new boolean[2 * numVars];
            int postOrder[] = new int[2 * numVars];
            postOrderCounter = 2 * numVars;

            for (int i = 0; i < 2 * numVars; i++) {
                if (!visited[i]) {
                    buildPostOrder(gR, i, visited, postOrder);
                }
            }

            visited = new boolean[2 * numVars];

            for (Integer componentStart: postOrder) {
                if (!visited[componentStart]){
                    HashSet<Integer> newComponent = new HashSet<Integer>();
                    buildCurrentComponent(componentStart, g, visited, newComponent);
                    components.add(newComponent);
                }
            }

            for (HashSet<Integer> component: components) {
                for (Integer vertex: component) {
                    if (vertex >= numVars && component.contains(vertex - numVars)){
                        return false;
                    }
                    if (vertex < numVars && component.contains(vertex + numVars)){
                        return false;
                    }
                }
            }

            for (HashSet<Integer> component: components) {
                for (Integer var: component) {
                    int reducedVar = var >= numVars ? var - numVars : var;
                    if (result[reducedVar] == -1){
                        result[reducedVar] = var >= numVars ? 1 : 0;
                    }
                }
            }

            return true;
        }

        private void buildCurrentComponent(Integer v, ArrayList<Integer>[] g, boolean[] visited, HashSet<Integer> currComponent) {
            visited[v] = true;
            currComponent.add(v);
            for (Integer w : g[v]) {
                if (!visited[w]){
                    buildCurrentComponent(w, g, visited, currComponent);
                }
            }
        }

        private void buildPostOrder(ArrayList<Integer>[] gR, int v, boolean[] visited, int[] postOrder) {
            visited[v] = true;
            for (Integer w : gR[v]) {
                if (!visited[w]) {
                    buildPostOrder(gR, w, visited, postOrder);
                }
            }
            postOrder[--postOrderCounter] = v;
        }

        private void buildImplicationGraph(ArrayList<Integer>[] g, ArrayList<Integer>[] gR) {
            for (Clause clause : clauses) {
                int l1 = clause.firstVar > 0 ? Math.abs(clause.firstVar) : Math.abs(clause.firstVar) + numVars;
                int l2 = clause.secondVar > 0 ? Math.abs(clause.secondVar) : Math.abs(clause.secondVar) + numVars;
                int notL1 = clause.firstVar < 0 ? Math.abs(clause.firstVar) : Math.abs(clause.firstVar) + numVars;
                int notL2 = clause.secondVar < 0 ? Math.abs(clause.secondVar) : Math.abs(clause.secondVar) + numVars;
                g[--notL1].add(--l2);
                g[--notL2].add(--l1);
                gR[l2].add(notL1);
                gR[l1].add(notL2);
            }
        }
    }

    public void run() {
        int n = reader.nextInt();
        int m = reader.nextInt();

        TwoSatisfiability twoSat = new TwoSatisfiability(n, m);
        for (int i = 0; i < m; ++i) {
            twoSat.clauses[i].firstVar = reader.nextInt();
            twoSat.clauses[i].secondVar = reader.nextInt();
        }

        int result[] = new int[n];
        Arrays.fill(result, -1);
        if (twoSat.isSatisfiable(result)) {
            writer.printf("SATISFIABLE\n");
            for (int i = 1; i <= n; ++i) {
                if (result[i - 1] == 1) {
                    writer.printf("%d", -i);
                } else {
                    writer.printf("%d", i);
                }
                if (i < n) {
                    writer.printf(" ");
                } else {
                    writer.printf("\n");
                }
            }
        } else {
            writer.printf("UNSATISFIABLE\n");
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
//        InputReader reader = new InputReader(new FileInputStream("files/CircuitDesign.txt"));
        InputReader reader = new InputReader(System.in);
        new Thread(null, new Runnable() {
            public void run() {
                try {
                    OutputWriter writer = new OutputWriter(System.out);
                    new CircuitDesign(reader, writer).run();
                    writer.writer.flush();
                } catch(Exception e) {
                }
            }
        }, "1", 1 << 26).start();
    }

    private final InputReader reader;
    private final OutputWriter writer;

    public CircuitDesign(InputReader reader, OutputWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    private static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public double nextDouble() {
            return Double.parseDouble(next());
        }

        public long nextLong() {
            return Long.parseLong(next());
        }
    }

    private static class OutputWriter {
        public PrintWriter writer;

        OutputWriter(OutputStream stream) {
            writer = new PrintWriter(stream);
        }

        public void printf(String format, Object... args) {
            writer.print(String.format(Locale.ENGLISH, format, args));
        }
    }
}
