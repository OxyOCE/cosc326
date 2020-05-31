import java.util.*;

public class Util {
    public static String sanitise(String n, int base) {
        int i = 0;

        if (n.length() > 0 && n.charAt(0) == '-') {
            i++;
        }

        if (base > 1) {
            if (i > 0 && n.length() == 1 || n.length() == 0) {
                return null;
            }

            for (; i < n.length(); i++) {
                try {
                    Integer.parseInt(Character.toString(n.charAt(i)), base);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        } else {
            for (; i < n.length(); i++) {
                if (n.charAt(i) != '1') {
                    return null;
                }
            }
        }

        return n;
    }

    public static int[] strToArr(String s) {
        int strIdx = 0;
        int size = s.length();

        if (size > 0 && s.charAt(0) == '-') {
            strIdx++;
            size--;
        }

        for (; strIdx < s.length() - 1; strIdx++) {
            if (s.charAt(strIdx) != '0') {
                break;
            }

            size--;
        }

        int[] res = new int[size];
        int arrIdx = 0;

        for (; strIdx < s.length(); strIdx++) {
            res[arrIdx++] = Integer.parseInt(Character.toString(s.charAt(strIdx)));
        }

        return res;
    }

    // Source: https://www.geeksforgeeks.org/reverse-an-array-in-java/
    public static int[] reverse(int[] arr) {
        int tmp;

        for (int i = 0; i < arr.length / 2; i++) {
            tmp = arr[i];
            arr[i] = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = tmp;
        }

        return arr;
    }

    public static void printArray(int[] arr, boolean isPositive) {
        if (!isPositive) {
            System.out.print("-");
        }

        for (int digit : arr) {
            System.out.print(Integer.toString(digit));
        }

        System.out.println();
    }

    public static int magnitude(int[] n1, int[] n2) {
        if (n1.length > n2.length) {
            return 1;
        }
        if (n2.length > n1.length) {
            return -1;
        }

        for (int i = 0; i < n1.length; i++) {
            if (n1[i] > n2[i]) {
                return 1;
            }

            if (n2[i] > n1[i]) {
                return -1;
            }
        }

        return 0;
    }

    public static int[] add(int[] n1, int[] n2, int base) {
        String ret = "";

        if (base == 1) {
            for (int i = 0; i < n1.length; i++) {
                ret += "1";
            }

            for (int i = 0; i < n2.length; i++) {
                ret += "1";
            }

            return strToArr(ret);
        }

        n1 = reverse(n1);
        n2 = reverse(n2);

        int carry = 0;
        for (int i = 0; i < n2.length; i++) {
            int sum = n2[i] + n1[i] + carry;
            if (sum >= base) {
                carry = 1;
            } else {
                carry = 0;
            }

            ret += Character.forDigit(sum % base, base);
        }

        for (int i = n2.length; i < n1.length; i++) {
            int sum = n1[i] + carry;
            if (sum >= base) {
                carry = 1;
            } else {
                carry = 0;
            }

            ret += Character.forDigit(sum % base, base);
        }

        if (carry > 0) {
            ret += (char)(carry + '0');
        }

        ret = new StringBuilder(ret).reverse().toString();

        return strToArr(ret);
    }

    public static int[] difference(int[] n1, int[] n2, int base) {
        String ret = "";

        if (base == 1) {
            for (int i = 0; i < (n1.length - n2.length); i++) {
                ret += "1";
            }

            return strToArr(ret);
        }

        n1 = reverse(n1);
        n2 = reverse(n2);

        int carry = 0;
        for (int i = 0; i < n2.length; i++) {
            int diff = n1[i] - n2[i] - carry;
            if (diff < 0) {
                diff += base;
                carry = 1;
            } else {
                carry = 0;
            }

            ret += Character.forDigit(diff, base);
        }

        for (int i = n2.length; i < n1.length; i++) {
            int diff = n1[i] - carry;
            if (diff < 0) {
                diff += base;
                carry = 1;
            } else {
                carry = 0;
            }

            ret += Character.forDigit(diff, base);
        }

        ret = new StringBuilder(ret).reverse().toString();

        int i = 0;
        while (i < ret.length() && ret.charAt(i) == '0') {
            i++;
        }

        ret = ret.substring(i);
        if (ret.length() == 0) {
            ret = "0";
        }

        return strToArr(ret);
    }

    public static void divide(int[] n, int base, boolean isPositive) {
        String res = "";

        if (base == 1) {
            for (int i = 0; i < n.length / 2; i++) {
                res += "1";
            }

            System.out.println("Quotient: " + (!isPositive && res.length() > 0 ? "-" : "") + res);
            System.out.println("Remainder: " + (n.length % 2 == 0 ? "" : "1"));

            return;
        }

        int remainder = 0;
        for (int i = 0; i < n.length; i++) {
            res += n[i] / 2;
            if (n[i] % 2 == 1) {
                if (i < n.length - 1) {
                    n[i + 1] += base;
                } else {
                    remainder = 1;
                }
            }
        }

        int i = 0;
        while (i < res.length() && res.charAt(i) == '0') {
            i++;
        }

        res = res.substring(i);

        if (res.length() == 0) {
            res = "0";
        }

        System.out.println("Quotient: " + (!isPositive && res.length() > 0 && res.charAt(0) != '0' ? "-" : "") + res);
        System.out.println("Remainder: " + remainder);
    }
}
