package week03;

import java.io.*;
import java.util.Locale;
import java.util.StringTokenizer;

public class GSMNetwork {
    private class Edge {
        int from;
        int to;
    }

    private class ConvertGSMNetworkProblemToSat {
        private int numVertices;
        private Edge[] edges;

        public ConvertGSMNetworkProblemToSat(int n, int m) {
            numVertices = n;
            edges = new Edge[m];
            for (int i = 0; i < m; ++i) {
                edges[i] = new Edge();
            }
        }

        //color offsets from original vertex index
        private int red(int vertex){
            return vertex;
        }
        private int green(int vertex){
            return vertex + numVertices;
        }
        private int blue(int vertex){
            return vertex + 2 * numVertices;
        }

        public void printEquisatisfiableSatFormula() {

            int numberOfClausesToEnsureOneColorOnVertices = numVertices * 4;
            int numberOfClausesForEdges = 3 * edges.length;

            int C = numberOfClausesToEnsureOneColorOnVertices + numberOfClausesForEdges;
            int V = numVertices * 3;

            writer.printf(C + " " + V + "\n");

            //make sure every vertex is single colored, for 3 color vertex versions only one can be true
            for (int vertex = 1; vertex <= numVertices; vertex++) {
                //need at least one 1
                writer.printf(red(vertex) + " " + green(vertex) + " " + blue(vertex) + " 0\n");
                //can not have both 1 - make sure only one get 1 - with negated combination of pairs
                writer.printf(-red(vertex) + " " + -green(vertex) + " 0\n");
                writer.printf(-red(vertex) + " " + -blue(vertex) + " 0\n");
                writer.printf(-green(vertex) + " " + -blue(vertex) + " 0\n");
            }

            for (int edge = 0; edge < edges.length; edge++) {
                int from = edges[edge].from;
                int to = edges[edge].to;

                writer.printf(-red(from) + " " + -red(to) + " 0\n");
                writer.printf(-green(from) + " " + -green(to) + " 0\n");
                writer.printf(-blue(from) + " " + -blue(to) + " 0\n");
            }
        }
    }

    private final InputReader reader;
    private final OutputWriter writer;

    public GSMNetwork(InputReader reader, OutputWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputReader reader = new InputReader(new FileInputStream("files/GSMNetwork.txt"));
//        InputReader reader = new InputReader(System.in);
        OutputWriter writer = new OutputWriter(System.out);
        new GSMNetwork(reader, writer).run();
        writer.writer.flush();
    }

    private void run() {
        int n = reader.nextInt();
        int m = reader.nextInt();
        ConvertGSMNetworkProblemToSat converter = new ConvertGSMNetworkProblemToSat(n, m);
        for (int i = 0; i < m; ++i) {
            converter.edges[i].from = reader.nextInt();
            converter.edges[i].to = reader.nextInt();
        }
        converter.printEquisatisfiableSatFormula();
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
