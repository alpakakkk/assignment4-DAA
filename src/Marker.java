import java.util.*;

/** Missions 3 and 4: exact string matching on DNA sequences. */
public class Marker {

    /**
     * Mission 3a: pi[i] is the length of the longest proper prefix of p[0..i]
     * that is also a suffix of p[0..i].
     * Example: ACGAC -> [0, 0, 0, 1, 2].
     */
    public static int[] prefixFunction(String p) {
        int m = p.length();
        int[] pi = new int[m];

        for (int i = 1; i < m; i++) {
            int matched = pi[i - 1];
            while (matched > 0 && p.charAt(i) != p.charAt(matched)) {
                matched = pi[matched - 1];
            }
            if (p.charAt(i) == p.charAt(matched)) {
                matched++;
            }
            pi[i] = matched;
        }
        return pi;
    }

    /**
     * Mission 3b: KMP returns every match start, including overlapping matches.
     * The text index i only moves forward. On a mismatch or after a match,
     * pi tells us how many pattern letters can already be reused.
     */
    public static int[] findAll(String text, String p) {
        // Empty markers are not genes, so there are no reported locations.
        if (p.isEmpty()) {
            return new int[0];
        }

        int[] pi = prefixFunction(p);
        List<Integer> starts = new ArrayList<>();
        int matched = 0;

        for (int i = 0; i < text.length(); i++) {
            while (matched > 0 && text.charAt(i) != p.charAt(matched)) {
                matched = pi[matched - 1];
            }
            if (text.charAt(i) == p.charAt(matched)) {
                matched++;
            }
            if (matched == p.length()) {
                starts.add(i - p.length() + 1);
                // Do not reset to zero: the next occurrence can overlap.
                matched = pi[matched - 1];
            }
        }

        int[] result = new int[starts.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = starts.get(i);
        }
        return result;
    }

    /** Mission 4: implemented in a separate commit. */
    public static int sharedWords(String a, String b, int k) {
        return -1;
    }
}
