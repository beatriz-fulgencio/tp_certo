import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

class FibHeapNode {
    int from, to, weight, id;
    FibHeapNode parent = null;
    List<FibHeapNode> children = new LinkedList<>();
    List<FibHeapNode> childListIterator;
    boolean isLoser = false;

    public FibHeapNode(int from, int to, int weight, int id) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        this.id = id;
    }
}

class Gabow {
    private CompressedTree compressedTree;
    private List<FibHeapNode> activeEdges;
    private List<List<FibHeapNode>> activeSets;

    public Gabow(CompressedTree compressedTree) {
        this.compressedTree = compressedTree;
        activeEdges = new ArrayList<>(compressedTree.parent.length);
        activeSets = new ArrayList<>(compressedTree.parent.length);
        for (int i = 0; i < compressedTree.parent.length; i++) {
            activeSets.add(new ArrayList<>());
        }
    }

    public void makeActive(int from, int to, int weight, int id) {
        // System.out.println("entrou");
        int fromRepresentative = compressedTree.find(from);
        if (activeEdges.get(fromRepresentative) == null) {
            activeEdges.set(fromRepresentative, new FibHeapNode(from, to, weight, id));
            return;
        }

        FibHeapNode v = activeEdges.get(fromRepresentative);
        if (weight + compressedTree.findValue(to) < currentWeight(v)
                || compressedTree.find(to) != compressedTree.find(v.to)) {
            removeFromCurrentList(v);
            v.to = to;
            v.weight = weight;
            v.id = id;
            v.from = from;
            moveHome(v);
        }
    }

    public void deleteActiveEdge(int i) {
        FibHeapNode v = activeEdges.get(i);

        for (FibHeapNode child : v.children)
            moveHome(child);
        v.children.clear();

        removeFromCurrentList(v);
        activeEdges.set(i, null);
    }

    public int getMin(int i) {
        List<FibHeapNode> activeSet = activeSets.get(i);
        int minWeight = Integer.MAX_VALUE;

        for (FibHeapNode node : activeSet) {
            if (node.weight < minWeight) {
                minWeight = node.weight;
            }
        }

        return minWeight;
    }

    public void mergeHeaps(int i, int j) {
        activeSets.get(i).addAll(activeSets.get(j));
        activeSets.get(j).clear();
    }

    public List<FibHeapNode> homeHeap(FibHeapNode v) {
        return activeSets.get(compressedTree.find(v.to));
    }

    public void removeFromCurrentList(FibHeapNode v) {
        List<FibHeapNode> list = v.parent != null ? v.parent.children : homeHeap(v);
        list.remove(v);
        if (v.parent != null) {
            loseChild(v.parent);
            v.parent = null;
        }
    }

    public void moveHome(FibHeapNode v) {
        List<FibHeapNode> home = homeHeap(v);
        home.add(v);
        v.parent = null;
    }

    public void loseChild(FibHeapNode v) {
        if (v.parent == null)
            return;
        if (v.isLoser) {
            loseChild(v.parent);
            v.parent.children.remove(v.childListIterator);
            moveHome(v);
        }
        v.isLoser = !v.isLoser;
    }

    public int currentWeight(FibHeapNode v) {
        return v.weight + compressedTree.findValue(v.to);
    }

    public static void main(String[] args) {
        int numVertices = 4;
        Graph graph = new Graph(numVertices);

        CompressedTree compressedTree = new CompressedTree(numVertices);
        Gabow activeForest = new Gabow(compressedTree);

        // add edges
        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(3, 1, 4);
        graph.addEdge(2, 3, 2);

        int id = 0; 

        for (Edge edge : graph.getEdges()) {
            int from = edge.src.id;
            int to = edge.dest.id;
            int weight = edge.weight;
            activeForest.makeActive(from, to, weight, id);
            id++;
            System.out.println(+ from + "-" + to + " (" + weight + ")");
        }        
    }
}

class CompressedTree {
    public int[] parent;
    private int[] rank;
    private int[] value;

    public CompressedTree(int size) {
        parent = new int[size];
        rank = new int[size];
        value = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            rank[i] = 0;
            value[i] = 0;
        }
    }

    public int find(int x) {
        if (x != parent[x]) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public void join(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX != rootY) {
            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
        }
    }

    public void addValue(int x, int val) {
        value[x] = val;
    }

    public int findValue(int x) {
        return value[x];
    }
}
