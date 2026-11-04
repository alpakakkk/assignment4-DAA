import java.util.*;

/**
 * Mission 5. Tree DP for a minimum vertex cover and DFS for tree diameter.
 * Every undirected edge must have at least one selected (tested) endpoint.
 * The input is a tree, rooted logically in person 0 during traversal.
 */
public class Outbreak {

    /**
     * Finds the cheapest set of tests and reconstructs a boolean selection.
     * include[v] = cheapest cost of v's subtree if v is tested.
     * exclude[v] = cheapest cost of v's subtree if v is NOT tested.
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        boolean[] tested = new boolean[n];
        if (n == 0) {
            return tested;
        }

        long[] include = new long[n];
        long[] exclude = new long[n];
        computeCosts(0, -1, nb, cost, include, exclude);

        // With equal costs, prefer including a vertex in a tie.
        boolean chooseRoot = include[0] <= exclude[0];
        rebuildTests(0, -1, chooseRoot, nb, include, exclude, tested);
        return tested;
    }

    /** Bottom-up DFS: process every child before computing its parent's states. */
    private static void computeCosts(int v, int parent, int[][] nb, int[] cost,
                                     long[] include, long[] exclude) {
        include[v] = cost[v];
        exclude[v] = 0;

        for (int child : nb[v]) {
            if (child == parent) {
                continue; // Edges are undirected; don't return to the parent.
            }
            computeCosts(child, v, nb, cost, include, exclude);

            // If v is tested, child may be tested or not.
            include[v] += Math.min(include[child], exclude[child]);
            // If v is not tested, every child MUST be tested.
            exclude[v] += include[child];
        }
    }

    /** Top-down reconstruction: choose the state that produced the optimum. */
    private static void rebuildTests(int v, int parent, boolean selected,
                                     int[][] nb, long[] include, long[] exclude,
                                     boolean[] tested) {
        tested[v] = selected;
        for (int child : nb[v]) {
            if (child == parent) {
                continue;
            }
            boolean chooseChild = !selected || include[child] <= exclude[child];
            rebuildTests(child, v, chooseChild, nb, include, exclude, tested);
        }
    }

    /**
     * Tree diameter (number of edges) in O(n).
     * At every vertex, a longest path passing through it is the sum of
     * the two deepest downward branches; track the best of these sums.
     */
    public static int longestChain(int n, int[][] nb) {
        if (n == 0) {
            return 0;
        }
        int[] diameter = {0};
        findDepth(0, -1, nb, diameter);
        return diameter[0];
    }

    /** Returns the deepest downward distance from v, counted in edges. */
    private static int findDepth(int v, int parent, int[][] nb, int[] diameter) {
        int deepest = 0;
        int second = 0;

        for (int child : nb[v]) {
            if (child == parent) {
                continue;
            }
            int depth = 1 + findDepth(child, v, nb, diameter);
            if (depth > deepest) {
                second = deepest;
                deepest = depth;
            } else if (depth > second) {
                second = depth;
            }
        }

        diameter[0] = Math.max(diameter[0], deepest + second);
        return deepest;
    }
}
