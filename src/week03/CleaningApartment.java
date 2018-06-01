package week03;

import java.io.*;
import java.util.Locale;
import java.util.StringTokenizer;

public class CleaningApartment {
    private class Edge {
        int from;
        int to;
    }

    private class ConvertHampathToSat {
        int numVertices;
        Edge[] edges;

        public ConvertHampathToSat(int n, int m) {
            numVertices = n;
            edges = new Edge[m];
            for (int i = 0; i < m; ++i) {
                edges[i] = new Edge();
            }
        }

        public void printEquisatisfiableSatFormula() {
            StringBuilder CNF4SAT = new StringBuilder();
            int C = 0, V = numVertices * numVertices;

            for (int i = 1; i < numVertices * numVertices + 1; i += numVertices) {
                for (int j = 0; j < numVertices; j++) {
                    CNF4SAT.append((i + j) + " ");
                }
                CNF4SAT.append("0\n");
                C++;
            }


            for (int i = 1; i < numVertices + 1; i++) {
                for (int j = 0; j < numVertices * numVertices; j += numVertices) {
                    CNF4SAT.append((i + j) + " ");
                }
                CNF4SAT.append("0\n");
                C++;
            }

            for (int i = 1; i < numVertices * numVertices + 1; i += numVertices) {
                for (int j = 0; j < numVertices; j++) {
                    for (int k = j + 1; k < numVertices; k++) {
                        CNF4SAT.append(-(i + j)+ " " + (-(i + k)) + " 0\n");
                        C++;
                    }
                }
            }

            for (int i = 1; i < numVertices + 1; i++) {
                for (int j = 0; j < numVertices * numVertices; j += numVertices) {
                    for (int k = j + numVertices; k < numVertices * numVertices; k += numVertices) {
                        CNF4SAT.append((-(i + j)) + " " + (-(i + k)) + " 0\n");
                        C++;
                    }
                }

            }


            boolean[][] adjacencyMatrix = new boolean[numVertices][numVertices];
            for (Edge edge : edges) {
                adjacencyMatrix[edge.from - 1][edge.to - 1] = true;
                adjacencyMatrix[edge.to - 1][edge.from - 1] = true;
            }
            for (int i = 0; i < numVertices; i++) {
                for (int j = i + 1; j < numVertices; j++) {
                    if (!adjacencyMatrix[i][j]) {
                        for (int k = 0; k < numVertices - 1; k++) {
                            CNF4SAT.append(-((i + 1) * numVertices - (numVertices - 1) + k)+ " " + (-((j + 1) * numVertices - (numVertices - 1) + k + 1)) + " 0\n");
                            CNF4SAT.append(-((j + 1) * numVertices - (numVertices - 1) + k) + " " + (-((i + 1) * numVertices - (numVertices - 1) + k + 1)) + " 0\n");
                            C += 2;
                        }
                    }
                }
            }

            writer.printf(C + " " + V + "\n");
            writer.printf(CNF4SAT.toString());
        }
    }

    private final InputReader reader;
    private final OutputWriter writer;

    public CleaningApartment(InputReader reader, OutputWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputReader reader = new InputReader(System.in);
//        InputReader reader = new InputReader(new FileInputStream("files/CleaningApartment.txt"));
        OutputWriter writer = new OutputWriter(System.out);
        new CleaningApartment(reader, writer).run();
        writer.writer.flush();
    }

    public void run() {
        int n = reader.nextInt();
        int m = reader.nextInt();

        ConvertHampathToSat converter = new ConvertHampathToSat(n, m);
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
