import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final int BOARD_SIZE = 8;
    private static final int NUM_SHIPS = 3;
    private static final int[] SHIP_SIZES = { 1, 2, 2 };

    private char[][] board;
    private long startTime;

    public static void main(String[] args) {
        Main game = new Main();
        game.start();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("Выберите действие:");
            System.out.println("1. Новая игра");
            System.out.println("2. Результаты");
            System.out.println("3. Выход");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    newGame();
                    break;
                case 2:
                    showResults();
                    break;
                case 3:
                    exit = true;
                    break;
                default:
                    System.out.println("Некорректный выбор. Попробуйте еще раз.");
                    break;
            }
        }

        scanner.close();
    }

    private void newGame() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = '-';
            }
        }

        placeShips();

        startTime = System.currentTimeMillis();

        boolean gameOver = false;
        Scanner scanner = new Scanner(System.in);

        while (!gameOver) {
            displayBoard();

            System.out.print("Куда стреляем: ");
            String input = scanner.nextLine().toUpperCase();

            if (input.length() < 2) {
                System.out.println("Некорректный ввод. Попробуйте еще раз.");
                continue;
            }

            char colChar = input.charAt(0);
            char rowChar = input.charAt(1);

            if (colChar < 'A' || colChar > 'H' || rowChar < '1' || rowChar > '8') {
                System.out.println("Некорректный ввод. Попробуйте еще раз.");
                continue;
            }

            int col = colChar - 'A';
            int row = rowChar - '1';

            if (board[row][col] == '-') {
                System.out.println("Промах!");
                board[row][col] = 'o';
            } else if (board[row][col] == 'S') {
                System.out.println("Попадание!");
                board[row][col] = 'U';

                if (isShipDestroyed(row, col)) {
                    System.out.println("Корабль уничтожен!");
                    markShipDestroyed(row, col);
                }

                if (allShipsDestroyed()) {
                    long currentTime = System.currentTimeMillis();
                    long elapsedTime = (currentTime - startTime) / 1000;
                    System.out.println("Поздравляю! Вы победили!");
                    System.out.println("Время: " + elapsedTime + " сек.");
                    gameOver = true;
                }
            } else {
                System.out.println("Вы уже стреляли в эту ячейку. Попробуйте еще раз.");
            }
        }

        scanner.close();
    }

    private void placeShips() {
        Random random = new Random();

        for (int size : SHIP_SIZES) {
            boolean shipPlaced = false;

            while (!shipPlaced) {
                int col = random.nextInt(BOARD_SIZE);
                int row = random.nextInt(BOARD_SIZE);

                boolean horizontal = random.nextBoolean();

                if (canPlaceShip(row, col, size, horizontal)) {
                    placeShip(row, col, size, horizontal);
                    shipPlaced = true;
                }
            }
        }
    }

    private boolean canPlaceShip(int startRow, int startCol, int size, boolean horizontal) {
        if (horizontal) {
            if (startCol + size > BOARD_SIZE) {
                return false;
            }

            for (int col = startCol; col < startCol + size; col++) {
                if (board[startRow][col] != '-') {
                    return false;
                }
            }
        } else {
            if (startRow + size > BOARD_SIZE) {
                return false;
            }

            for (int row = startRow; row < startRow + size; row++) {
                if (board[row][startCol] != '-') {
                    return false;
                }
            }
        }

        return true;
    }

    private void placeShip(int startRow, int startCol, int size, boolean horizontal) {
        if (horizontal) {
            for (int col = startCol; col < startCol + size; col++) {
                board[startRow][col] = 'S';
            }
        } else {
            for (int row = startRow; row < startRow + size; row++) {
                board[row][startCol] = 'S';
            }
        }
    }

    private boolean isShipDestroyed(int row, int col) {
        char cell = board[row][col];

        if (cell != 'U') {
            return false;
        }

        // Check left
        if (col > 0 && board[row][col - 1] == 'S') {
            return false;
        }

        // Check right
        if (col < BOARD_SIZE - 1 && board[row][col + 1] == 'S') {
            return false;
        }

        // Check above
        if (row > 0 && board[row - 1][col] == 'S') {
            return false;
        }

        // Check below
        if (row < BOARD_SIZE - 1 && board[row + 1][col] == 'S') {
            return false;
        }

        return true;
    }

    private void markShipDestroyed(int row, int col) {
        // Mark the destroyed ship
        board[row][col] = 'X';

        // Mark the surrounding cells as 'o'
        // Check left
        if (col > 0 && board[row][col - 1] != 'X') {
            board[row][col - 1] = 'o';
        }

        // Check right
        if (col < BOARD_SIZE - 1 && board[row][col + 1] != 'X') {
            board[row][col + 1] = 'o';
        }

        // Check above
        if (row > 0 && board[row - 1][col] != 'X') {
            board[row - 1][col] = 'o';
        }

        // Check below
        if (row < BOARD_SIZE - 1 && board[row + 1][col] != 'X') {
            board[row + 1][col] = 'o';
        }
    }

    private boolean allShipsDestroyed() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 'S') {
                    return false;
                }
            }
        }

        return true;
    }

    private void displayBoard() {
        System.out.print("  ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print((char) ('A' + i) + " ");
        }
        System.out.println();

        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void showResults() {
        // Implement logic to show the top 3 fastest games
        System.out.println("Топ 3 самых быстрых игр:");
        // Your implementation here

        System.out.println();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Нажмите Enter, чтобы вернуться в главное меню.");
        scanner.nextLine();
    }
}
