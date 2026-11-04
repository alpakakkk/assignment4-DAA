/**
 * Missions 1 and 2. Compare two DNA strains with dynamic programming.
 * Rows correspond to prefixes of a; columns correspond to prefixes of b.
 */
public class Strains {

    /**
     * Mission 1: Levenshtein edit distance.
     * d[i][j] is the minimum number of insertions, deletions, and replacements
     * needed to transform the first i characters of a into the first j of b.
     */
    public static int editDistance(String a, String b) {
        int n = a.length();
        int m = b.length();
        int[][] d = new int[n + 1][m + 1];

        // Empty-to-prefix: insert j characters; prefix-to-empty: delete i.
        for (int i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int replaceCost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                int delete = d[i - 1][j] + 1;
                int insert = d[i][j - 1] + 1;
                int replace = d[i - 1][j - 1] + replaceCost;
                d[i][j] = Math.min(replace, Math.min(delete, insert));
            }
        }
        return d[n][m];
    }

    /**
     * Mission 2: longest common subsequence length.
     * d[i][j] is the LCS length of the first i characters of a
     * and the first j characters of b; unlike a substring, gaps are allowed.
     */
    public static int lcsLength(String a, String b) {
        // TODO Mission 2: intentionally completed in the next commit.
        return -1;
    }
}
