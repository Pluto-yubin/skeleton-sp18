package hw3.hash;

import java.util.List;

public class OomageTestUtility {
    public static boolean haveNiceHashCodeSpread(List<Oomage> oomages, int M) {
        /*
         * Write a utility function that returns true if the given oomages
         * have hashCodes that would distribute them fairly evenly across
         * M buckets. To do this, convert each oomage's hashcode in the
         * same way as in the visualizer, i.e. (& 0x7FFFFFFF) % M.
         * and ensure that no bucket has fewer than N / 50
         * Oomages and no bucket has more than N / 2.5 Oomages.
         */
        int[] bucketsCount = new int[M];
        for (int i = 0; i < oomages.size(); i++) {
            int index = (oomages.get(i).hashCode() & 0x7FFF_FFFF) % M;
            bucketsCount[index] += 1;
        }
        int N = oomages.size();
        for (int i = 0; i < M; i++) {
            int count = bucketsCount[i];
            if (count > oomages.size() / 2.5 || count < oomages.size() / 50.0) {
                return false;
            }
        }
        return true;
    }
}
