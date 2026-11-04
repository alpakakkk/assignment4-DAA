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

    // A rolling polynomial hash; multiplication stays within a signed long.
    private static final long BASE = 257L;
    private static final long MOD = 1_000_000_007L;

    /** Returns the hash of the first k characters in s. */
    private static long firstHash(String s, int k) {
        long hash = 0;
        for (int i = 0; i < k; i++) {
            hash = (hash * BASE + s.charAt(i)) % MOD;
        }
        return hash;
    }

    /** Removes the outgoing character and adds the incoming one in O(1). */
    private static long roll(long oldHash, char outgoing, char incoming,
                             long highestPower) {
        long withoutFirst = (oldHash - outgoing * highestPower % MOD + MOD) % MOD;
        return (withoutFirst * BASE + incoming) % MOD;
    }

    /** Verifies characters explicitly: hash equality alone is insufficient. */
    private static boolean sameWindow(String a, int i, String b, int j, int k) {
        for (int offset = 0; offset < k; offset++) {
            if (a.charAt(i + offset) != b.charAt(j + offset)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Mission 4: Rabin-Karp counts *positions in a*, not unique substrings.
     * Hash each length-k window of b. For every window of a, inspect only
     * positions with the same hash and verify the actual letters.
     * Hashes for adjacent windows are computed in constant time.
     */
    public static int sharedWords(String a, String b, int k) {
        if (k <= 0 || a.length() < k || b.length() < k) {
            return 0;
        }

        long highestPower = 1;
        for (int i = 1; i < k; i++) {
            highestPower = highestPower * BASE % MOD;
        }

        Map<Long, List<Integer>> positionsByHash = new HashMap<>();
        long bHash = firstHash(b, k);
        for (int i = 0; i <= b.length() - k; i++) {
            positionsByHash.computeIfAbsent(bHash, key -> new ArrayList<>()).add(i);
            if (i < b.length() - k) {
                bHash = roll(bHash, b.charAt(i), b.charAt(i + k), highestPower);
            }
        }

        int count = 0;
        long aHash = firstHash(a, k);
        for (int i = 0; i <= a.length() - k; i++) {
            List<Integer> candidates = positionsByHash.get(aHash);
            if (candidates != null) {
                for (int j : candidates) {
                    if (sameWindow(a, i, b, j, k)) {
                        count++; // Count this starting position in a only once.
                        break;
                    }
                }
            }
            if (i < a.length() - k) {
                aHash = roll(aHash, a.charAt(i), a.charAt(i + k), highestPower);
            }
        }
        return count;
    }
}
