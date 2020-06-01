import java.util.*;

public class Scenario {
    public int[] singleCol, doubleCol, singleRow, doubleRow;
    public int rowSize, colSize;

    public Scenario(String singleCol, String doubleCol, String singleRow, String doubleRow) {
        this.singleCol = strToArr(singleCol);
        this.doubleCol = strToArr(doubleCol);
        this.singleRow = strToArr(singleRow);
        this.doubleRow = strToArr(doubleRow);
        this.rowSize = this.singleRow.length;
        this.colSize = this.singleCol.length;
    }

    public static int[] strToArr(String s) {
        String[] arr = s.split(" ");
        int[] ret = new int[arr.length];

        for (int i = 0; i < arr.length; i++) {
            ret[i] = Integer.parseInt(arr[i]);
        }

        return ret;
    }

    public static List<Scenario> bootstrap() {
        List<Scenario> ret = new ArrayList<Scenario>();
        List<String> sanitisedInput = new ArrayList<String>();
        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            if (line.length() == 0) {
                continue;
            }

            if (line.charAt(0) == '#') {
                continue;
            }

            sanitisedInput.add(line);
        }
        sc.close();

        for (int i = 0; i < sanitisedInput.size();) {
            String singleCol = sanitisedInput.get(i++);
            String doubleCol = sanitisedInput.get(i++);
            String singleRow = sanitisedInput.get(i++);
            String doubleRow = sanitisedInput.get(i++);

            Scenario sTemp = new Scenario(singleCol, doubleCol, singleRow, doubleRow);
            ret.add(sTemp);
        }

        return ret;
    }

    public static char[][] getBoard(Scenario s) {
        char[][] board = new char[s.rowSize][s.colSize];

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = '\0';
            }
        }

        return board;
    }

    public static void printBoard(char[][] b) {
        for (int row = b.length - 1; row >= 0; row--) {
            for (int col = 0; col < b[row].length; col++) {
                switch (b[row][col]) {
                    case 't':
                    case 'b':
                        System.out.print('|');
                        break;
                    case 'l':
                    case 'r':
                        System.out.print('-');
                        break;
                    default: System.out.print(b[row][col]);
                }
            }

            System.out.println();
        }
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < singleCol.length;) {
            ret.append(Integer.toString(singleCol[i++]));
            if (i == singleCol.length) {
                break;
            }
            ret.append(" ");
        }
        ret.append("\n");
        for (int i = 0; i < doubleCol.length;) {
            ret.append(Integer.toString(doubleCol[i++]));
            if (i == doubleCol.length) {
                break;
            }
            ret.append(" ");
        }
        ret.append("\n");
        for (int i = 0; i < singleRow.length;) {
            ret.append(Integer.toString(singleRow[i++]));
            if (i == singleRow.length) {
                break;
            }
            ret.append(" ");
        }
        ret.append("\n");
        for (int i = 0; i < doubleRow.length;) {
            ret.append(Integer.toString(doubleRow[i++]));
            if (i == doubleRow.length) {
                break;
            }
            ret.append(" ");
        }

        return ret.toString();
    }
}
