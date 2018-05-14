package week01;

import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;

class AirlineCrews {
    private FastScanner in;
    private PrintWriter out;

    public static void main(String[] args) throws IOException {
        new AirlineCrews().solve();
    }

    public void solve() throws IOException {
        in = new FastScanner();
        out = new PrintWriter(new BufferedOutputStream(System.out));
        boolean[][] bipartiteGraph = readData();
        int[] matching = findMatching(bipartiteGraph);
        writeResponse(matching);
        out.close();
    }

    boolean[][] readData() throws IOException {
        numFlights = in.nextInt();
        numCrew = in.nextInt();
        boolean[][] adjMatrix = new boolean[numFlights][numCrew];
        for (int i = 0; i < numFlights; ++i)
            for (int j = 0; j < numCrew; ++j)
                adjMatrix[i][j] = (in.nextInt() == 1);
        return adjMatrix;
    }

    private int numFlights;
    private int numCrew;

    public int[] findMatching(boolean[][] bipartiteGraph) {
        int[] matching = new int[numFlights];
        Arrays.fill(matching, -1);

        for (int crew = 0; crew < numCrew; crew++) {
            boolean[] visited = new boolean[numFlights];
            bipartiteMatching(bipartiteGraph, crew, visited, matching);
        }

        return matching;
    }

    private boolean bipartiteMatching(boolean[][] bipartiteGraph, int crew, boolean[] visited, int[] matching) {
        for (int flight = 0; flight < numFlights; flight++) {
            if (bipartiteGraph[flight][crew]){
                if (!visited[flight]){
                    visited[flight] = true;
                    if (matching[flight] == -1 || bipartiteMatching(bipartiteGraph, matching[flight], visited, matching)){
                        matching[flight] = crew;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void writeResponse(int[] matching) {
        for (int i = 0; i < matching.length; ++i) {
            if (i > 0) {
                out.print(" ");
            }
            if (matching[i] == -1) {
                out.print("-1");
            } else {
                out.print(matching[i] + 1);
            }
        }
        out.println();
    }

    static class FastScanner {
        private BufferedReader reader;
        private StringTokenizer tokenizer;

        public FastScanner() {
            reader = new BufferedReader(new InputStreamReader(System.in));
            tokenizer = null;
        }

        public String next() throws IOException {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                tokenizer = new StringTokenizer(reader.readLine());
            }
            return tokenizer.nextToken();
        }

        public int nextInt() throws IOException {
            return Integer.parseInt(next());
        }
    }
}
