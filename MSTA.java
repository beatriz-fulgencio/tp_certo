import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

class Vertex {
    public int id;

    Vertex(int id) {
        this.id = id;
    }
}

class Edge {
    public int weight;
    public Vertex src, dest;

    Edge(int s, int d, int w) {
        src = new Vertex(s);
        dest = new Vertex(d);
        weight = w;
    }

    public Edge(Vertex src2, Vertex dest2, int weig) {
        src = src2;
        dest = dest2;
        weight = weig;
    }
}

class Graph {
    private int V; // Número de vértices
    private Map<Integer, List<Vertex>> adj; // Lista de adjacência do grafo
    private List<Edge> edges; // Lista de arestas do grafo

    public int getV() {
        return V;
    }

    public Map<Integer, List<Vertex>> getAdj() {
        return adj;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    Graph(int v) {
        V = v;
        adj = new HashMap<>(v);

        edges = new ArrayList<>();
    }

    public Graph(int r, int i) {
        V = i;
        adj = new HashMap<>();
        adj.put(r, new ArrayList<>());
        edges = null;
    }

    // Adiciona uma aresta ao grafo
    void addEdge(int src, int dest, int weig) {
        try {
            adj.get(src).add(new Vertex(dest));
        } catch (Exception e) {
            adj.put(src, new ArrayList<>());
            adj.get(src).add(new Vertex(dest));
        }
        edges.add(new Edge(new Vertex(src), new Vertex(dest), weig));
    }

    public boolean dfs(int v, int parent, boolean[] visited, Stack<Integer> stack, List<Integer> cycle) {
        visited[v] = true;
        stack.push(v);

        for (Vertex neighbor : adj.get(v)) {
            if (!visited[neighbor.id]) {
                if (dfs(neighbor.id, v, visited, stack, cycle)) {
                    return true;
                }
            } else if (neighbor.id != parent) {
                // Encontramos um ciclo
                while (!stack.isEmpty()) {
                    int vertex = stack.pop();
                    cycle.add(vertex);
                    if (vertex == neighbor.id) {
                        break;
                    }
                }

                return true;
            }
        }

        stack.pop();
        return false;
    }

    public void addAdj(int v2) {
        adj.get(v2).add(null);
    }

    public void addEdge(Vertex src, Vertex dest, int weight) {
        adj.get(src.id).add(dest);
        edges.add(new Edge(src, dest, weight));
    }

}

public class MSTA {

    static ArrayList<Edge> originalEdges = new ArrayList<>();
    static ArrayList<Edge> resultEdges = new ArrayList<>();

    // Função para verificar a presença de ciclos negativos usando Bellman-Ford
    static boolean hasNegativeCycles(int source, Graph g) {
        int[] dist = new int[g.getV()];
        int[] parent = new int[g.getV()];
        for (int i = 0; i < g.getV(); i++) {
            dist[i] = Integer.MAX_VALUE;
            parent[i] = -1;
        }
        dist[source] = 0;

        // Relaxamento das arestas V-1 vezes
        for (int i = 0; i < g.getV() - 1; i++) {
            for (Edge edge : g.getEdges()) {
                int u = edge.src.id;
                int v = edge.dest.id;
                if (dist[u] != Integer.MAX_VALUE && dist[u] + 1 < dist[v]) {
                    dist[v] = dist[u] + 1;
                    parent[v] = u;
                }
            }
        }

        // Verificar a presença de ciclos negativos
        for (Edge edge : g.getEdges()) {
            int u = edge.src.id;
            int v = edge.dest.id;
            if (dist[u] != Integer.MAX_VALUE && dist[u] + 1 < dist[v]) {
                return true; // Ciclo negativo encontrado
            }
        }

        return false; // Não há ciclos negativos
    }

    public Edge findMinimumIncomingEdge(int vertex, Graph g) {
        Edge minimumEdge = null;

        for (Edge edge : g.getEdges()) {
            if (edge.dest.id == vertex) {
                if (minimumEdge == null || edge.weight < minimumEdge.weight) {
                    minimumEdge = edge;
                }
            }
        }

        return minimumEdge;
    }

    public static Map<Integer, Edge> findMinimumCycle(int source, Graph g) {
        Map<Integer, Edge> cicloMinimo = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        Stack<Edge> stack1 = new Stack<>();
        Stack<Integer> stack2 = new Stack<>();

        boolean hasCycle = dfs(source, visited, stack1, stack2, cicloMinimo, g);

        if (!hasCycle) {
            return null; // Não há ciclo mínimo
        }

        return cicloMinimo;
    }

    private static boolean dfs(int v, Set<Integer> visited, Stack<Edge> stack, Stack<Integer> stack2,
            Map<Integer, Edge> cicloMinimo, Graph g) {
        visited.add(v);
        stack2.push(v);
        // System.out.println("vertex:"+v);

        for (Edge edge : g.getEdges()) {
            if (edge.src.id == v) {
                int neighbor = edge.dest.id;

                // System.out.println("vertex-neighboor:"+neighbor);

                stack.push(new Edge(v, neighbor, edge.weight));

                if (visited.contains(neighbor) && stack2.contains(neighbor)) {
                    while (!stack.isEmpty()) {
                        Edge vertex = stack.pop();
                        // System.out.println("pop:"+vertex.src.id +"->"+vertex.dest.id);
                        // ystem.out.println("add edge:"+edge.src.id + edge.dest.id);
                        cicloMinimo.put(vertex.src.id, new Edge(vertex.src.id, vertex.dest.id, vertex.weight));
                        if (vertex.src.id == neighbor) {
                            break;
                        }
                    }
                    return true;
                }

                if (!visited.contains(neighbor) && dfs(neighbor, visited, stack, stack2, cicloMinimo, g)) {
                    return true;
                }
            }
        }

        stack.pop();
        return false;
    }

    public static Graph reduceGraph(Map<Integer, Edge> cicloMinimo, Graph g) {
        Graph reducedGraph = new Graph(g.getV() - cicloMinimo.size() + 1);

        for (Edge e : cicloMinimo.values()) {
            System.out.println(e.src.id + "->" + e.dest.id);

        }

        for (Integer e : cicloMinimo.keySet()) {
            System.out.println(e + "--" + e);

        }

        for (Edge edge : g.getEdges()) {
            if (!(cicloMinimo.containsKey(edge.src.id) && edge.dest.id == cicloMinimo.get(edge.src.id).dest.id)) {
                int src = edge.src.id;
                int dest = edge.dest.id;
                int weight = edge.weight;

                if (cicloMinimo.containsKey(src)) {
                    src = cicloMinimo.values().iterator().next().src.id;
                }
                if (cicloMinimo.containsKey(dest)) {
                    dest = cicloMinimo.values().iterator().next().src.id;
                }

                // Se a aresta não está no ciclo mínimo, atualize o peso
                if (cicloMinimo.containsKey(src) && cicloMinimo.get(src).dest.id == dest) {
                    weight -= cicloMinimo.get(src).weight;
                }

                if (!(cicloMinimo.containsKey(src) && cicloMinimo.containsKey(dest)))
                    reducedGraph.addEdge(src, dest, weight);
            }
        }

        return reducedGraph;
    }

    public static List<Integer> Edmonds(Graph g, int r, Graph result, Graph origin, boolean isFirstTime) {

        System.out.println("isFirstTime: " + isFirstTime);

        if(isFirstTime) {
            for (Edge e : g.getEdges()) {
            System.out.print(e.src.id + "->" + e.dest.id + "(" + e.weight + ")");
            System.out.println();
            originalEdges.add(e);
        }
        }

        System.out.println("vertices: " + g.getV());

        for (Edge e : g.getEdges()) {
            System.out.print(e.src.id + "->" + e.dest.id + "(" + e.weight + ")");
            System.out.println();
        }
        isFirstTime= false;
        System.out.println();

        Map<Integer, Edge> cycle = findMinimumCycle(r, g);

        for (Integer integer : cycle.keySet()) {
            System.out.print(integer + " - ");
        }

        System.out.println();

        Graph redGraph = reduceGraph(cycle, g);

        // System.out.println(redGraph.getV() + "redsize");

        System.out.println(redGraph.getEdges().size() + "e-size");

        for (Edge e : redGraph.getEdges()) {
            System.out.print(e.src.id + "->" + e.dest.id + "(" + e.weight + ")");
            System.out.println();
        }
        // System.out.println("e-size");

        List<Integer> redResult;

        if (redGraph.getV() > 1) {
            // System.out.println(redGraph.getV()+"size");
            redResult = Edmonds(redGraph, redGraph.getEdges().get(0).src.id, result, origin, isFirstTime);
        } else {
            redResult = new ArrayList<>();
            redResult.add(r);
        }

        // Map<Integer, Integer> result = ReconstructMSTA(redResult, cycle, r);

        // List<Integer> resultVertices = new ArrayList<>();
        // for (Map.Entry<Integer, Integer> entry : result.entrySet()) {
        // resultVertices.add(entry.getKey());
        // resultVertices.add(entry.getValue());
        // }

        System.out.println("---");

        for (Edge e : g.getEdges()) {
            System.out.print(e.src.id + "->" + e.dest.id + "(" + e.weight + ")");
            System.out.println();
        }

        int minWeight = Integer.MAX_VALUE;
        Vertex minWeightDest = null;
        for (Edge e : g.getEdges()) {
            if (e.weight < minWeight) {
                minWeight = e.weight;
                minWeightDest = e.dest;
            }
        }

        System.out.println("aresta de menor peso " + minWeight + " tem o vertice de destino " + minWeightDest.id);


        System.out.println("---");


        System.out.println("aresta original");
        for (Edge e : originalEdges) {
            if(e.weight == minWeight && e.dest.id == minWeightDest.id) {
                System.out.println(e.src.id + "->" + e.dest.id + "(" + e.weight + ")");
                resultEdges.add(e);
            }
        }

        // return resultVertices;

        return null;
    }

    // static Map<Integer, Integer> ReconstructMSTA(List<Integer> result_reduzido, Map<Integer, Edge> ciclo_minimo,
    //         int raiz) {
    //     Map<Integer, Integer> result = new HashMap<>();

    //     for (int v : result_reduzido) {
    //         if (ciclo_minimo.keySet().contains(v)) {
    //             result.put(v, raiz);
    //         } else {
    //             result.put(v, v);
    //         }
    //     }

    //     return result;
    // }

    public static void main(String[] args) {
        int V = 5; // Número de vértices
        Graph graph = new Graph(V);

        // Adicione arestas ao grafo

        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(3, 1, 4);
        graph.addEdge(2, 3, 2);
        // graph.addEdge(3, 2, 5);
        // graph.addEdge(4, 2, 5);

        int source = 0;

        Graph res = new Graph(graph.getEdges().size());

        boolean isFirstTime = true;

        Edmonds(graph, source, res, graph, isFirstTime);

        // if (result == null) {
        //     System.out.println("Sem Solução");
        // } else {
            System.out.println("Resultado da MSTA:");
            for (Edge e : resultEdges) {
                System.out.println(e.src.id + "->" + e.dest.id + " (" + e.weight + ")");
            }
        // }
    }

}
