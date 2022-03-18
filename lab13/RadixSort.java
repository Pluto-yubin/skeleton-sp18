import java.util.Arrays;

/**
 * Class for doing Radix sort
 *
 * @author Akhil Batra, Alexander Hwang
 *
 */
public class RadixSort {
    /**
     * Does LSD radix sort on the passed in array with the following restrictions:
     * The array can only have ASCII Strings (sequence of 1 byte characters)
     * The sorting is stable and non-destructive
     * The Strings can be variable length (all Strings are not constrained to 1 length)
     *
     * @param asciis String[] that needs to be sorted
     *
     * @return String[] the sorted array
     */
    public static String[] sort(String[] asciis) {
        String[] sorted = Arrays.copyOf(asciis, asciis.length);
        int maxLen = 0;
        for (int i = 0; i < asciis.length; i++) {
            maxLen = Math.max(maxLen, asciis[i].length());
        }
        for (int i = 0; i < maxLen; i++) {
            int[] count = new int[256];
            for (int j = 0; j < asciis.length; j++) {
                String s = asciis[j];
                int pos = s.length() <= i ? 0 : s.charAt(i);
                count[pos] += 1;
            }

            int[] starter = new int[256];
            int pos = 0;
            for (int j = 0; j < starter.length; j++) {
                starter[j] = pos;
                pos += count[j];
            }
            for (String s : Arrays.copyOf(sorted, sorted.length)) {
                int index = s.length() <= i ? 0 : s.charAt(i);
                int place = starter[index];
                sorted[place] = s;
                starter[index] += 1;
            }
        }
        return sorted;
    }

    /**
     * LSD helper method that performs a destructive counting sort the array of
     * Strings based off characters at a specific index.
     * @param asciis Input array of Strings
     * @param index The position to sort the Strings on.
     */
    private static void sortHelperLSD(String[] asciis, int index) {
        return;
    }

    /**
     * MSD radix sort helper function that recursively calls itself to achieve the sorted array.
     * Destructive method that changes the passed in array, asciis.
     *
     * @param asciis String[] to be sorted
     * @param start int for where to start sorting in this method (includes String at start)
     * @param end int for where to end sorting in this method (does not include String at end)
     * @param index the index of the character the method is currently sorting on
     *
     **/
    private static void sortHelperMSD(String[] asciis, int start, int end, int index) {
        // Optional MSD helper method for optional MSD radix sort
        return;
    }

    public static void main(String[] args) {
        String[] strings = {"z", "`", "]", "Ò"};
        System.out.println(2e9 < 2147483647);
        for (String s : strings) {
            System.out.println((int) s.charAt(0));
        }
        RadixSort.sort(strings);
    }
}
