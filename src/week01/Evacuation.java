package week01;

import java.io.*;
import java.util.*;

public class Evacuation {
    private static FastScanner in;

    public static void main(String[] args) throws IOException {
        in = new FastScanner();

        FlowGraph graph = readGraph();
        System.out.println(maxFlow(graph, 0, graph.size() - 1));
    }

    private static int maxFlow(FlowGraph graph, int from, int to) {
        return new Flow(graph, from, to).getFlow();
    }

    private static class Flow {
        private boolean[] visited;
        private Edge[] edgeTo;
        private int flow = 0;
        private int N;

        public Flow(FlowGraph graph, int source, int sink) {
            this.N = graph.size();
            while (findPathIfExists(graph, source, sink)) {
                int minCut = Integer.MAX_VALUE;
                //path exists - first find min cut value !!
                for (int currentVertex = sink; currentVertex != source; currentVertex = edgeTo[currentVertex].from) {
                    Edge edge = edgeTo[currentVertex];
                    minCut = Math.min(minCut, edge.getCapacityWithinResidualGraph());
                }

                //update edges flow with minCut value
                for (int currentVertex = sink; currentVertex != source; currentVertex = edgeTo[currentVertex].from) {
                    Edge edge = edgeTo[currentVertex];
                    graph.addFlow(edge.id, minCut);
                }

                //update graph flow with minCut value
                flow += minCut;
            }
        }

        private boolean findPathIfExists(FlowGraph graph, int source, int sink) {
            Queue<Integer> vertices = new LinkedList<Integer>();
            this.visited = new boolean[N];
            this.edgeTo = new Edge[N];

            this.visited[source] = true;
            vertices.add(source);

            while (!vertices.isEmpty() && !visited[sink]) {
                Integer v = vertices.remove();
                for (Integer id : graph.getIds(v)) {
                    Edge e = graph.getEdge(id);
                    if (e.getCapacityWithinResidualGraph() > 0 && !this.visited[e.to]) {
                        this.visited[e.to] = true;
                        vertices.add(e.to);
                        this.edgeTo[e.to] = e;
                    }
                }
            }

            return visited[sink];
        }

        public int getFlow() {
            return this.flow;
        }
    }

    static class Edge {
        int from, to, capacity, flow, id;

        public Edge(int from, int to, int capacity, int id) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.flow = 0;
            this.id = id;
        }

        public int getCapacityWithinResidualGraph() {
            return capacity - flow;
        }
    }

    /* This class implements a bit unusual scheme to store the graph edges, in order
     * to retrieve the backward edge for a given edge quickly. */
    static class FlowGraph {
        /* List of all - forward and backward - edges */
        private List<Edge> edges;

        /* These adjacency lists store only indices of edges edgeTo the edges list */
        private List<Integer>[] graph;

        public FlowGraph(int n) {
            this.graph = (ArrayList<Integer>[]) new ArrayList[n];
            for (int i = 0; i < n; ++i)
                this.graph[i] = new ArrayList<>();
            this.edges = new ArrayList<>();
        }

        public void addEdge(int from, int to, int capacity) {
            /* Note that we first append a forward edge and then a backward edge,
             * so all forward edges are stored at even indices (starting edgeTo 0),
             * whereas backward edges are stored at odd indices. */
            Edge forwardEdge = new Edge(from, to, capacity, edges.size());
            graph[from].add(edges.size());
            edges.add(forwardEdge);
            Edge backwardEdge = new Edge(to, from, 0, edges.size());
            graph[to].add(edges.size());
            edges.add(backwardEdge);
        }

        public int size() {
            return graph.length;
        }

        public List<Integer> getIds(int from) {
            return graph[from];
        }

        public Edge getEdge(int id) {
            return edges.get(id);
        }

        public void addFlow(int id, int flow) {
            /* To get a backward edge for a true forward edge (i.e id is even), we should get id + 1
             * due to the described above scheme. On the other hand, when we have to get a "backward"
             * edge for a backward edge (i.e. get a forward edge for backward - id is odd), id - 1
             * should be taken.
             *
             * It turns out that id ^ 1 works for both cases. Think this through! */
            edges.get(id).flow += flow;
            edges.get(id ^ 1).flow -= flow;
        }
    }

    static FlowGraph readGraph() throws IOException {
        int vertex_count = in.nextInt();
        int edge_count = in.nextInt();
        FlowGraph graph = new FlowGraph(vertex_count);

        for (int i = 0; i < edge_count; ++i) {
            int from = in.nextInt() - 1, to = in.nextInt() - 1, capacity = in.nextInt();
            graph.addEdge(from, to, capacity);
        }
        return graph;
    }

    static class FastScanner {
        private BufferedReader reader;
        private StringTokenizer tokenizer;

        public FastScanner() throws FileNotFoundException {
            reader = new BufferedReader(new InputStreamReader(System.in));
//            reader = new BufferedReader(new InputStreamReader(new FileInputStream("files/week01.Evacuation.txt")));
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
