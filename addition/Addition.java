import java.util.*;

public class Addition {
    private static final int BASEERR = -1;

    public static void main(String[] args) {
        Scanner kbd = new Scanner(System.in);
        int base;
        String n1String, n2String;
        int[] n1Array, n2Array, sum;
        boolean n1IsPositive, n2IsPositive, sumIsPositive;

        try {
            while (true) {
                do {
                    System.out.println("What base are the numbers in?");
                    try {
                        base = Integer.parseInt(kbd.nextLine(), 10);
                    } catch (NumberFormatException e) {
                        base = BASEERR;
                    }
                    if (base < 1 || base > 10) {
                        System.out.println("Invalid base.");
                    }
                } while (base < 1 || base > 10);

                do {
                    System.out.println("Enter the first number:");
                    n1String = kbd.nextLine();
                    if (null == (n1String = Util.sanitise(n1String, base))) {
                        System.out.println("Invalid number.");
                    }
                } while (null == n1String);

                do {
                    System.out.println("Enter the second number:");
                    n2String = kbd.nextLine();
                    if (null == (n2String = Util.sanitise(n2String, base))) {
                        System.out.println("Invalid number.");
                    }
                } while (null == n2String);

                System.out.println();

                n1Array = Util.strToArr(n1String);
                if (n1String.length() > 0) {
                    n1IsPositive = n1String.charAt(0) == '-' ? false : true;
                } else {
                    n1IsPositive = true;
                }

                n2Array = Util.strToArr(n2String);
                if (n2String.length() > 0) {
                    n2IsPositive = n2String.charAt(0) == '-' ? false : true;
                } else {
                    n2IsPositive = true;
                }

                if (Util.magnitude(n1Array, n2Array) < 0) {
                    int[] nTmp = n1Array;
                    boolean nTmpIsPositive = n1IsPositive;

                    n1Array = n2Array;
                    n1IsPositive = n2IsPositive;

                    n2Array = nTmp;
                    n2IsPositive = nTmpIsPositive;
                }

                if ((n1IsPositive && n2IsPositive) || (!n1IsPositive && !n2IsPositive)) {
                    sum = Util.add(n1Array, n2Array, base);
                } else {
                    sum = Util.difference(n1Array, n2Array, base);
                }

                if (n1IsPositive) {
                    sumIsPositive = true;
                } else {
                    sumIsPositive = false;
                }

                if (base == 1) {
                     if (sum.length == 0 && sumIsPositive == false) {
                         sumIsPositive = true;
                     }
                } else if (sum[0] == 0 && sumIsPositive == false) {
                    sumIsPositive = true;
                }

                System.out.print(base == 1 ? "Tally: " : "Sum: ");
                Util.printArray(sum, sumIsPositive);
                Util.divide(sum, base, sumIsPositive);
                System.out.println();
            }
        } catch (Exception e) {
            // Keyboard interrupt
        } finally {
            kbd.close();
        }
    }
}
