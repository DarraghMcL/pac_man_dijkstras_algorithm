//Darragh McLernon
package Pacman;

import java.util.*;

public class UndirectedGraph implements GraphADT
{
    //declaring class variables

    private ArrayList nodes = new ArrayList();
    private int[][] adjMatrix;
    private int size;
    private static int nodeId = 0;
    public static final double INFINITY = Double.POSITIVE_INFINITY;

    //empty class constructor
    public UndirectedGraph()
    {
    }

    //constructor
    public UndirectedGraph(int size)
    {
        adjMatrix = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                adjMatrix[i][j] = -1;
            }
        }
    }

    //adds the specified node to the matrix
    public void addNode(Node n)
    {
        if (getSize() == adjMatrix.length) {
            expandCapacity();
        }
        nodes.add(n);
    }

    //returns the number of nodes
    public int getSize()
    {
        return nodes.size();
    }

    //sets edge between specified nodes and sets weight between them
    public void setEdge(Node start, Node end, int weight)
    {
        int startIndex = nodes.indexOf(start);
        int endIndex = nodes.indexOf(end);
        adjMatrix[startIndex][endIndex] = weight;
        adjMatrix[endIndex][startIndex] = weight;
        start.addNeighbor(end);
        end.addNeighbor(start);
    }

    //deletes edges between nodes
    public void deleteEdge(Node start, Node end)
    {
        int startIndex = nodes.indexOf(start);
        int endIndex = nodes.indexOf(end);
        adjMatrix[startIndex][endIndex] = -1;
        adjMatrix[endIndex][startIndex] = -1;
    }

    //returns true if edge exists between specified nodes
    public boolean edgeExists(Node start, Node end)
    {
        int startIndex = nodes.indexOf(start);
        int endIndex = nodes.indexOf(end);

        //weight is -1 for nodes with an edge
        if (adjMatrix[startIndex][endIndex] != -1) {
            return true;
        } else {
            return false;
        }
    }

    //gets the next unvisited child node
    private Node getUnvisitedChildNode(Node n)
    {
        int indexParent = nodes.indexOf(n);
        Iterator it = n.getNeighbors();

        while (it.hasNext()) {
            Node neigh = (Node) it.next();
            if (!neigh.isVisited()) {
                return neigh;
            }

        }
        return null;

    }

    //Breadth-first traversal of the tree
    //not needed for shortest path but is here for abstract class
    public void bfs(Node src)
    {
        QueueADT q = new LinkedQueue();
        q.enqueue(src);
        printNode(src);
        src.setVisited(true);
        while (!q.isEmpty()) {
            Node n = (Node) q.dequeue();
            Node child = null;
            while ((child = getUnvisitedChildNode(n)) != null) {
                child.setVisited(true);
                printNode(child);
                q.enqueue(child);
            }
        }

        clearNodes();

    }

    //depth-first transversal of tree
    //not needed for shortest path but is here for abstract class
    public void dfs(Node src)
    {
        StackADT s = new LinkedStack();
        s.push(src);
        src.setVisited(true);
        printNode(src);
        while (!s.isEmpty()) {
            Node n = (Node) s.peek();
            Node child = getUnvisitedChildNode(n);
            if (child != null) {
                child.setVisited(true);
                printNode(child);
                s.push(child);
            } else {
                s.pop();
            }
        }
        clearNodes();
    }

    private void clearNodes()
    {
        int i = 0;
        while (i < getSize()) {
            Node n = (Node) nodes.get(i);
            n.setVisited(false);
            i++;
        }
    }

    private void printNode(Node n)
    {
        System.out.println(n.getLabel() + " ");
    }

    //display the graph
    public void dumpGraph()
    {
        dump(adjMatrix);
    }

    //generates a text version of the graph
    public void dump(int a[][])
    {
        int size = 1012;
        System.out.println("  ");

        for (int s = 0; s < size; s = s + 1) {
            System.out.print((Node) nodes.get(s));
            System.out.println();
        }

        for (int s = 0; s < size; s = s + 1) {
            System.out.print((Node) nodes.get(s));

            for (int e = 0; e < size; e = e + 1) {
                if (a[s][e] == -1) {
                    System.out.print("F" + "\t");
                } else {
                    System.out.print("T" + "\t");
                }
            }
            System.out.println();

        }
    }

    //extends the capacity of the array
    private void expandCapacity()
    {
        int currentSize = adjMatrix.length;

        int[][] larger = new int[currentSize + 1][currentSize + 1];

        for (int i = 0; i < currentSize; i++) {
            for (int j = 0; j < currentSize; j++) {
                larger[i][j] = adjMatrix[i][j];
            }
        }
        adjMatrix = larger;
    }

    //returns the weight between two nodes
    public int getEdgeWeight(Node start, Node end)
    {
        int startIndex = nodes.indexOf(start);
        int endIndex = nodes.indexOf(end);

        if (adjMatrix[startIndex][endIndex] == -1) {
            return 5000000;
        }

        return adjMatrix[startIndex][endIndex];
    }

    //Dijkras algorithm
    public Node getShortestDistance(Node src, Node dst)
    {
        if (src == null || dst == null) {
            return null;
        }

        final int[] pathWeights = new int[1012];
        Node[] predecessors = new Node[1012];


        //initialisation
        //size is hardcoded to number of cells in array
        for (int i = 0; i < 1012; i++) {
            pathWeights[i] = 500000;
            predecessors[i] = null;
        }

        clearNodes();

        pathWeights[src.getId()] = 0;

        Iterator<Node> it;
        Node temp = src;
        Node neigh;

        //compares two nodes based on weight
        final Comparator<Node> comp = new Comparator<Node>()
        {

            public int compare(Node n1, Node n2)
            {
                if (pathWeights[n1.getId()] > pathWeights[n2.getId()]) {
                    return 1;
                }

                if (pathWeights[n1.getId()] < pathWeights[n2.getId()]) {
                    return -1;
                }

                return 0;

            }
        };

        //setting up a new queue
        PriorityQueue<Node> nodeQueue = new PriorityQueue<Node>(6, comp);
        nodeQueue.add(src);

        //fills the queue with nodes
        while (!temp.equals(dst) && !nodeQueue.isEmpty()) {
            temp = nodeQueue.poll();
            if (!temp.isVisited()) {
                temp.setVisited(true);
                it = temp.getNeighbors();
                while (it.hasNext()) {
                    neigh = it.next();
                    if (pathWeights[neigh.getId()] > pathWeights[temp.getId()] + getEdgeWeight(temp, neigh)) {
                        pathWeights[neigh.getId()] = pathWeights[temp.getId()] + getEdgeWeight(temp, neigh);
                        predecessors[neigh.getId()] = temp;
                    }
                    nodeQueue.add(neigh);
                }
            }
        }

        //constructing the shortest path
        Vector<Node> pathVec = new Vector<Node>();
        Node step = dst;
        while (step != null) {
            pathVec.add(step);
            step = predecessors[step.getId()];
        }

        if (!(pathVec.get(pathVec.size() - 1)).equals(src)) {
            System.out.println("Cannot find shortest path");
        }

        //returns the second node in the shortest path
        //if path is less than two steps, returns the last step
        if (pathVec.size() < 3) {
            return pathVec.get(0);
        }
        return pathVec.get(pathVec.size() - 2);
    }
}