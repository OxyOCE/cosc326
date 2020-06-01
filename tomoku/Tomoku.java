import java.util.*;

public class Tomoku {

    private static List<Scenario> scenarios;
    private static char[][] board;
    private static Scenario s;
    private static char searchBy;

    public static void main(String[] args) {
        scenarios = Scenario.bootstrap();

        for (Scenario tmp : scenarios) {
            s = tmp;
            board = Scenario.getBoard(s);

            System.out.println(s + "\n");
            if (board.length > 1 && board[0].length > 1) {
                if (board.length > board[0].length && doTomokuByRow(0, 0)) {
                    Scenario.printBoard(board);
                } else if (doTomokuByCol(0, 0)) {
                    Scenario.printBoard(board);
                } else {
                    System.out.println("Not achievable");
                }
            } else {
                System.out.println("Not achievable");
            }
            System.out.println();
        }
    }

    // Inspiration: https://www.geeksforgeeks.org/sudoku-backtracking-7/
    private static boolean doTomokuByRow(int sX, int sY) {
        int row = -1;
        int col = -1;
        boolean complete = true;

        int x = sX;
        int y = sY;
        while (x < board.length) {
            while (y < board[x].length) {
                if (board[x][y] == '\0') {
                    row = x;
                    col = y;
                    complete = false;

                    break;
                }

                y++;
            }

            if (!complete) {
                break;
            }
            x++;
            y = 0;
        }

        if (complete) {
            return true;
        }

        if (putSquare(row, col)) {
            if (luckyMove(row, col) && doTomokuByRow(row, col + 1)) {
                return true;
            } else {
                takeSquare(row, col);
            }
        }

        if (putHorizon(row, col)) {
            if (luckyMove(row, col) && doTomokuByRow(row, col + 2)) {
                return true;
            } else {
                takeHorizon(row, col);
            }
        }

        if (putVertical(row, col)) {
            if (luckyMove(row, col) && doTomokuByRow(row, col + 1)) {
                return true;
            } else {
                takeVertical(row, col);
            }
        }

        return false;
    }

    private static boolean doTomokuByCol(int sX, int sY) {
        int row = -1;
        int col = -1;
        boolean complete = true;

        int x = sX;
        int y = sY;
        while (y < board[0].length) {
            while (x < board.length) {
                if (board[x][y] == '\0') {
                    row = x;
                    col = y;
                    complete = false;

                    break;
                }

                x++;
            }

            if (!complete) {
                break;
            }
            y++;
            x = 0;
        }

        if (complete) {
            return true;
        }

        if (putSquare(row, col)) {
            if (luckyMove(row, col) && doTomokuByCol(row + 1, col)) {
                return true;
            } else {
                takeSquare(row, col);
            }
        }

        if (putVertical(row, col)) {
            if (luckyMove(row, col) && doTomokuByCol(row + 2, col)) {
                return true;
            } else {
                takeVertical(row, col);
            }
        }

        if (putHorizon(row, col)) {
            if (luckyMove(row, col) && doTomokuByCol(row + 1, col)) {
                return true;
            } else {
                takeHorizon(row, col);
            }
        }

        return false;
    }

    // Check the board for any unlucky corners (i.e. where 4 sectors' seams form a cross).
    private static boolean luckyMove(int row, int col) {
        if (!luckyQuadrant(row - 1, col - 1)) {
            return false;
        }

        return true;
    }

    private static boolean luckyQuadrant(int r, int c) {
        return !fullSectors(r, c)
        || !uniqueMats(r, c, r, c + 1)
        || !uniqueMats(r, c + 1, r + 1, c + 1)
        || !uniqueMats(r + 1, c, r + 1, c + 1)
        || !uniqueMats(r, c, r + 1, c);
    }

    // Checks if each sector in a quadrant is not null.
    private static boolean fullSectors(int r, int c) {
        if (r < 0 || r >= board.length - 1 || c < 0 || c >= board[r].length - 1) {
            return false;
        }
        
        return board[r][c] != '\0' && board[r][c + 1] != '\0' && board[r + 1][c] != '\0' && board[r + 1][c + 1] != '\0';
    }

    // Do the two squares contain unique mats?
    private static boolean uniqueMats(int r1, int c1, int r2, int c2) {
        if (r2 > r1 && (board[r1][c1] == 't' && board[r2][c2] == 'b')) {
            return false;
        }

        if (c2 > c1 && (board[r1][c1] == 'l' && board[r2][c2] == 'r')) {
            return false;
        }

        return true;
    }

    private static boolean putSquare(int row, int col) {
        if (s.singleRow[row] > 0 && s.singleCol[col] > 0) {
            s.singleRow[row]--;
            s.singleCol[col]--;
            board[row][col] = 'o';

            return true;
        }

        return false;
    }

    private static boolean putHorizon(int row, int col) {
        if (s.doubleRow[row] > 0 && col < board[row].length - 1 && board[row][col + 1] == '\0') {
            s.doubleRow[row]--;
            board[row][col] = 'l';
            board[row][col + 1] = 'r';

            return true;
        }

        return false;
    }

    private static boolean putVertical(int row, int col) {
        if (s.doubleCol[col] > 0 && row < board.length - 1 && board[row + 1][col] == '\0') {
            s.doubleCol[col]--;
            board[row][col] = 't';
            board[row + 1][col] = 'b';

            return true;
        }

        return false;
    }

    private static void takeSquare(int row, int col) {
        s.singleRow[row]++;
        s.singleCol[col]++;
        board[row][col] = '\0';
    }

    private static void takeHorizon(int row, int col) {
        s.doubleRow[row]++;
        board[row][col] = '\0';
        board[row][col + 1] = '\0';
    }

    private static void takeVertical(int row, int col) {
        s.doubleCol[col]++;
        board[row][col] = '\0';
        board[row + 1][col] = '\0';
    }
}
