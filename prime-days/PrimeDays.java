public class PrimeDays {
    private static boolean[] soe;

    public static void main(String[] args) {
        int[] months = new int[args.length];

        for (int i = 0; i < args.length; i++) {
            months[i] = Integer.valueOf(args[i]);
        }

        int highestPrimeMonth = nextLowestPrime(months.length);

        populateSoe(sumToLength(months, highestPrimeMonth));

        int dayOfYear = 0;
        for (int month = 0; month < highestPrimeMonth; month++) {
            if (!isPrime(month + 1)) {
                dayOfYear += months[month];
                continue;
            }

            for (int dayOfMonth = 0; dayOfMonth < months[month]; dayOfMonth++) {
                if (isPrime(dayOfMonth + 1) && isPrime(dayOfYear + 1)) {
                    System.out.println((dayOfYear + 1) + ": " + (month + 1) + " " + (dayOfMonth + 1));
                }

                dayOfYear++;
            }
        }
    }

    private static boolean isPrime(int x) {
        return soe[x];
    }

    private static int sumToLength(int[] numbers, int length) {
        int sum = 0;

        for (int i = 0; i < length; i++) {
            sum += numbers[i];
        }

        return sum;
    }

    // Sieve of Erastothenes
    // Pseudocode sourced from www.geeksforgeeks.org/sieve-of-eratosthenes
    private static void populateSoe(int size) {
        soe = new boolean[size + 1];

        for (int i = 0; i < soe.length; i++) {
            soe[i] = true;
        }

        for (int i = 2; i < soe.length && i * i <= size; i++) {
            if (soe[i]) {
                for (int j = i * i; j <= size; j += i) {
                    soe[j] = false;
                }
            }
        }

        soe[0] = false;
        if (size > 1) {
            soe[1] = false;
        }
    }

    // Pseudocode sources from https://en.wikipedia.org/wiki/Primality_test
    private static boolean isPrimeSingle(int x) {
        if (x <= 3) {
            return x > 1;
        }
        
        if (x % 3 == 0 || x % 2 == 0) {
            return false;
        }

        for (int y = 5; y * y <= x; y += 6) {
            if (x % y == 0 || x % (y + 2) == 0) {
                return false;
            }
        }

        return true;
    }

    private static int nextLowestPrime(int x) {
        int probe;
        if (x == 2) {
            return x;
        } else {
            probe = x % 2 == 0 ? x - 1 : x;
        }

        for (;probe > 1; probe -= 2) {
            if (isPrimeSingle(probe)) {
                return probe;
            }
        }

        return -1;
    }
}
