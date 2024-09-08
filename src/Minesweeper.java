import java.util.*;

class Minesweeper {
    private static final int Mines = 10;
    private static final int n = 8;
    private static char[][] board = new char[n][n];
    private static char[][] sweep = new char[n][n];
    private static boolean[][] revealed = new boolean[n][n];
    private static final char empty = '-';
    private static final char bomb  = '*';
    private static final char cell  = '#';
    private static final String RESET = "\u001b[01m";
    private static final String BLUE = "\u001b[34m";
    private static final String RED = "\u001b[31m";
    private static final String NEON = "\u001b[36m";
    private static final String GREEN = "\u001b[32m";
    private static Set<String> set = new HashSet<>();

    public static void main(String[] args) {
        initialize();
        placeMines();
        playGame();
    }

    // Initializes the board and the sweep arrays
    private static void initialize() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = empty;
                sweep[i][j] = cell;
            }
        }
    }

    // Places mines randomly on the board
    private static void placeMines() {
        Random random = new Random();
        int count = 1;
        while (count <= Mines) {
            int row = random.nextInt(n);
            int col = random.nextInt(n);
            if (board[row][col] != '*') {
                board[row][col] = '*';
                set.add(row + ":" + col);  // For debugging, you can remove this line.
                count++;
            }
        }
    }

    // Starts the game and processes user inputs
    private static void playGame() {
        Scanner scanner = new Scanner(System.in);
        boolean gameOver = false;
        System.out.println(set);
        while (!gameOver) {
            printBoard();
            System.out.println("Enter (row col) or (f row col) to flag/unflag:");
            String input = scanner.nextLine();
            String[] fragment = input.split(" ");

            if (fragment.length == 3 && input.startsWith("f")) {
                toggleFlag(Integer.parseInt(fragment[1]), Integer.parseInt(fragment[2]));
            } else if (fragment.length == 2) {
                int row = Integer.parseInt(fragment[0]);
                int col = Integer.parseInt(fragment[1]);

                if (!isValidCell(row, col)) {
                    System.out.println("Invalid input! Try again.");
                    continue;
                }

                if (isMine(row, col)) {
                    System.out.println("You hit a mine! Game Over...");
                    revealAll();
                    printBoard();
                    gameOver = true;
                } else {
                    reveal(row, col);
                    if (checkWin()) {
                        System.out.println("Hurray! You win!");
                        gameOver = true;
                    }
                }
            } else {
                System.out.println("Invalid command! Use 'row col' or 'f row col'.");
            }
        }
    }

    // Checks if the player has won the game
    private static boolean checkWin() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (!revealed[i][j] && board[i][j] != '*') {
                    return false;
                }
            }
        }
        return true;
    }

    // Prints the game board
    private static void printBoard() {
        System.out.println("\t0 1 2 3 4 5 6 7");
        for (int i = 0; i < n; i++) {
            System.out.print(i + " | ");
            for (int j = 0; j < n; j++) {
                char c = sweep[i][j];
                switch (c) {
                    case cell:
                        System.out.print(NEON + c + " " + RESET);
                        break;
                    case bomb:
                        System.out.print(RED + c + " " + RESET);
                        break;
                    case empty:
                        System.out.print(BLUE + c + " " + RESET);
                        break;
                    case 'F':
                        System.out.print(GREEN + c + " " + RESET);
                        break;
                    default:
                        System.out.print(RED + c + " " + RESET);
                        break;
                }
            }
            System.out.println();
        }
    }

    // Checks if a cell contains a mine
    private static boolean isMine(int row, int col) {
        return board[row][col] == '*' && sweep[row][col] != 'F';
    }

    // Reveals a cell, recursively reveals surrounding cells if no adjacent mines
    private static void reveal(int row, int col) {
        if (!isValidCell(row, col) || revealed[row][col] || sweep[row][col] == 'F') {
            return;
        }

        revealed[row][col] = true;
        int count = getCount(row, col);
        if (count > 0) {
            sweep[row][col] = (char) (count + '0');  // Convert count to char
        } else {
            sweep[row][col] = empty;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    reveal(row + i, col + j);
                }
            }
        }
    }

    // Gets the number of mines adjacent to a cell
    private static int getCount(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newR = row + i;
                int newC = col + j;
                if (isValidCell(newR, newC) && board[newR][newC] == '*') {
                    count++;
                }
            }
        }
        return count;
    }

    // Reveals all mines when the game is over
    private static void revealAll() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == '*') {
                    sweep[i][j] = '*';
                }
            }
        }
    }

    // Toggles a flag on a cell
    private static void toggleFlag(int row, int col) {
        if (!isValidCell(row, col) || revealed[row][col]) {
            System.out.println("Invalid cell for flagging!");
            return;
        }
        sweep[row][col] = (sweep[row][col] == 'F') ? cell : 'F';
    }

    // Checks if a cell is within the valid bounds of the board
    private static boolean isValidCell(int row, int col) {
        return row >= 0 && row < n && col >= 0 && col < n;
    }
}
