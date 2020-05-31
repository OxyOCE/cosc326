public class HouseNumbers {
    public static void main(String[] args) {
        long i = 1;
        long longestK = getK(i);
        long longestN = getN(i);
        while (longestN <= Integer.MAX_VALUE) {
            if (longestK < longestN) System.out.println("k: " + longestK + " n: " + longestN);
            i++;
            longestK = getK(i);
            longestN = getN(i);
        }
    }

    // Source: https://oeis.org/A001109
    private static long getK(long x) {
        if (x == 0) return 0;
        if (x == 1) return 1;
        return 6 * getK(x - 1) - getK(x - 2);
    }

    // Source: https://oeis.org/A001108
    private static long getN(long x) {
        if (x == 0) return 0;
        if (x == 1) return 1;
        return 6 * getN(x - 1) - getN(x - 2) + 2;
    }
}
