import java.util.*;

/**
 * Optional extra tests for edge cases and small random cases. No dependencies.
 * Run from project root after compiling src:
 *   javac -cp out -d out tools/ExtraTests.java
 *   java -cp out ExtraTests
 */
public class ExtraTests {
    private static final Random RANDOM = new Random(20261104);
    private static int checks = 0;

    public static void main(String[] args) {
        check(Strains.editDistance("", "AC") == 2, "edit empty");
        check(Strains.editDistance("GATTACA", "GCATGCA") == 3, "edit classic");
        check(Strains.lcsLength("", "AAC") == 0, "lcs empty");
        check(Strains.lcsLength("ACGTT", "AGTC") == 3, "lcs classical");
        check(Arrays.equals(Marker.findAll("AAAA", "AA"), new int[]{0, 1, 2}), "overlap KMP");
        check(Arrays.equals(Marker.findAll("AC", "G"), new int[0]), "KMP absent");
        check(Arrays.equals(Marker.prefixFunction("ACGAC"), new int[]{0, 0, 0, 1, 2}), "prefix example");
        check(Marker.sharedWords("AAAAA", "AAA", 2) == 4, "duplicate windows counted");
        check(Marker.sharedWords("ACG", "GCA", 12) == 0, "short strings");

        for (int trial = 0; trial < 250; trial++) {
            String a = dna(RANDOM.nextInt(9));
            String b = dna(RANDOM.nextInt(9));
            String p = dna(1 + RANDOM.nextInt(6));
            int k = 1 + RANDOM.nextInt(5);
            check(Strains.editDistance(a, b) == oracleEdit(a, b), "random edit #" + trial);
            check(Strains.lcsLength(a, b) == oracleLcs(a, b), "random LCS #" + trial);
            check(Arrays.equals(Marker.prefixFunction(p), oraclePrefix(p)), "random prefix #" + trial);
            check(Arrays.equals(Marker.findAll(a, p), oracleFindAll(a, p)), "random KMP #" + trial);
            check(Marker.sharedWords(a, b, k) == oracleSharedWords(a, b, k), "random rolling hash #" + trial);
        }

        for (int trial = 0; trial < 160; trial++) {
            int n = 1 + RANDOM.nextInt(10);
            int[][] nb = randomTree(n);
            int[] costs = new int[n];
            int[] ones = new int[n];
            Arrays.fill(ones, 1);
            for (int i = 0; i < n; i++) costs[i] = 1 + RANDOM.nextInt(9);
            boolean[] selected = Outbreak.cheapestTests(n, nb, costs);
            boolean[] fewest = Outbreak.cheapestTests(n, nb, ones);
            check(coversEveryEdge(nb, selected), "weighted edge coverage #" + trial);
            check(coversEveryEdge(nb, fewest), "unweighted edge coverage #" + trial);
            check(totalCost(selected, costs) == oracleVertexCover(nb, costs), "minimum testing cost #" + trial);
            check(totalCost(fewest, ones) == oracleVertexCover(nb, ones), "minimum number of tests #" + trial);
            check(Outbreak.longestChain(n, nb) == oracleDiameter(nb), "tree diameter #" + trial);
        }
        System.out.println("ExtraTests passed: " + checks + " assertions (random seed 20261104).");
    }

    private static void check(boolean ok, String description) {
        checks++;
        if (!ok) throw new AssertionError("Failed: " + description);
    }

    private static String dna(int n) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < n; i++) s.append("ACGT".charAt(RANDOM.nextInt(4)));
        return s.toString();
    }

    private static int oracleEdit(String a, String b) {
        int[][] memo = new int[a.length() + 1][b.length() + 1];
        for (int[] row : memo) Arrays.fill(row, -1);
        return editFrom(a, b, 0, 0, memo);
    }

    private static int editFrom(String a, String b, int i, int j, int[][] memo) {
        if (i == a.length()) return b.length() - j;
        if (j == b.length()) return a.length() - i;
        if (memo[i][j] >= 0) return memo[i][j];
        int replacement = (a.charAt(i) == b.charAt(j) ? 0 : 1)
                + editFrom(a, b, i + 1, j + 1, memo);
        int removal = 1 + editFrom(a, b, i + 1, j, memo);
        int addition = 1 + editFrom(a, b, i, j + 1, memo);
        return memo[i][j] = Math.min(replacement, Math.min(removal, addition));
    }

    private static int oracleLcs(String a, String b) {
        int best = 0;
        for (int mask = 0; mask < (1 << a.length()); mask++) {
            int j = 0;
            for (int i = 0; i < a.length(); i++) {
                if ((mask & (1 << i)) != 0) {
                    while (j < b.length() && b.charAt(j) != a.charAt(i)) j++;
                    if (j == b.length()) { j = -1; break; }
                    j++;
                }
            }
            if (j >= 0) best = Math.max(best, Integer.bitCount(mask));
        }
        return best;
    }

    private static int[] oraclePrefix(String p) {
        int[] pi = new int[p.length()];
        for (int i = 0; i < p.length(); i++) {
            for (int length = 1; length <= i; length++) {
                if (equalAt(p, 0, p, i - length + 1, length)) pi[i] = length;
            }
        }
        return pi;
    }

    private static int[] oracleFindAll(String t, String p) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i + p.length() <= t.length(); i++) {
            if (equalAt(t, i, p, 0, p.length())) result.add(i);
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    private static boolean equalAt(String a, int ia, String b, int ib, int length) {
        for (int j = 0; j < length; j++) if (a.charAt(ia + j) != b.charAt(ib + j)) return false;
        return true;
    }

    private static int oracleSharedWords(String a, String b, int k) {
        int count = 0;
        for (int i = 0; i + k <= a.length(); i++) {
            for (int j = 0; j + k <= b.length(); j++) {
                if (equalAt(a, i, b, j, k)) { count++; break; }
            }
        }
        return count;
    }

    private static int[][] randomTree(int n) {
        List<List<Integer>> tmp = new ArrayList<>();
        for (int i = 0; i < n; i++) tmp.add(new ArrayList<>());
        for (int i = 1; i < n; i++) {
            int parent = RANDOM.nextInt(i);
            tmp.get(i).add(parent);
            tmp.get(parent).add(i);
        }
        int[][] nb = new int[n][];
        for (int i = 0; i < n; i++) nb[i] = tmp.get(i).stream().mapToInt(Integer::intValue).toArray();
        return nb;
    }

    private static boolean coversEveryEdge(int[][] nb, boolean[] selected) {
        for (int i = 0; i < nb.length; i++) {
            for (int j : nb[i]) if (!selected[i] && !selected[j]) return false;
        }
        return true;
    }

    private static int totalCost(boolean[] selected, int[] cost) {
        int sum = 0;
        for (int i = 0; i < selected.length; i++) if (selected[i]) sum += cost[i];
        return sum;
    }

    private static int oracleVertexCover(int[][] nb, int[] cost) {
        int best = Integer.MAX_VALUE;
        for (int mask = 0; mask < (1 << nb.length); mask++) {
            boolean valid = true;
            int total = 0;
            for (int v = 0; v < nb.length; v++) {
                if ((mask & (1 << v)) != 0) total += cost[v];
                for (int u : nb[v]) {
                    if (u > v && (mask & (1 << v)) == 0 && (mask & (1 << u)) == 0) valid = false;
                }
            }
            if (valid) best = Math.min(best, total);
        }
        return best;
    }

    private static int oracleDiameter(int[][] nb) {
        int best = 0;
        for (int start = 0; start < nb.length; start++) {
            int[] dist = new int[nb.length];
            Arrays.fill(dist, -1);
            Queue<Integer> q = new ArrayDeque<>();
            q.add(start);
            dist[start] = 0;
            while (!q.isEmpty()) {
                int v = q.remove();
                best = Math.max(best, dist[v]);
                for (int u : nb[v]) {
                    if (dist[u] == -1) { dist[u] = dist[v] + 1; q.add(u); }
                }
            }
        }
        return best;
    }
}
