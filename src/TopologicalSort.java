// A Java program to print topological
// sorting of a DAG
import java.util.*;

// This class represents a directed graph
// using adjacency list representation
class TopologicalSort {
    // No. of vertices
    private int V;

    // Adjacency List as ArrayList of ArrayList's
    private ArrayList<ArrayList<Integer> > adj;

    // Constructor
    TopologicalSort(int v) {
        V = v;
        adj = new ArrayList<ArrayList<Integer> >(v);
        for (int i = 0; i < v; ++i)
            adj.add(new ArrayList<Integer>());
    }

    // Function to add an edge into the graph
    void addEdge(int v, int w) { adj.get(v).add(w); }

    // A recursive function used by topologicalSort
    void topologicalSortUtil(int v, boolean visited[],
                             Stack<Integer> stack)
    {
        // Mark the current node as visited.
        visited[v] = true;
        Integer i;

        // Recur for all the vertices adjacent
        // to thisvertex
        Iterator<Integer> it = adj.get(v).iterator();
        while (it.hasNext()) {
            i = it.next();
            if (!visited[i])
                topologicalSortUtil(i, visited, stack);
        }

        // Push current vertex to stack
        // which stores result
        Integer integer = Integer.valueOf(v);
        stack.push(integer);
    }

    // The function to do Topological Sort.
    // It uses recursive topologicalSortUtil()
    List<Integer> topologicalSort()
    {
        Stack<Integer> stack = new Stack<Integer>();

        // Mark all the vertices as not visited
        boolean visited[] = new boolean[V];
        for (int i = 0; i < V; i++)
            visited[i] = false;

        // Call the recursive helper
        // function to store
        // Topological Sort starting
        // from all vertices one by one
        for (int i = 0; i < V; i++)
            if (visited[i] == false)
                topologicalSortUtil(i, visited, stack);

        List<Integer> sortedVariables = new ArrayList<>();
        // Print contents of stack
        while (stack.empty() == false)
            sortedVariables.add(stack.pop());

        return sortedVariables;
    }

    public static HashMap<String, Integer> getVariablesWithNumber(List<Variable> variables) {
        HashMap<String, Integer> variablesWithNumber = new HashMap<>();
        for (int i = 0; i < variables.size(); i++) {
            variablesWithNumber.put(variables.get(i).getName(), i);
        }
        for (Map.Entry<String, Integer> v :variablesWithNumber.entrySet()) {
            System.out.println("key: "+v.getKey()+" value:"+v.getValue());
        }
        return variablesWithNumber;
    }

    public List<String> getSortedOrder(List<Integer> sortedVariables, HashMap<String, Integer> variablesWithNumber) {
        List<String> sortedOrder = new ArrayList<>();
        for(Map.Entry<String, Integer> entry: variablesWithNumber.entrySet()) {
            for (Integer v:sortedVariables) {
                if(entry.getValue() == v) {
                    sortedOrder.add(entry.getKey());
                    break;
                }
            }
        }
        return sortedOrder;
    }

}
