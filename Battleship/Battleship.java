package battleship;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public final class Battleship {
    public static void main(String[] args) {
        new Game();
    }
}

final class Game {
    private final Scanner scanner = new Scanner(System.in);
    private Player player1;
    private Player currentPlayer;
    private Player enemyPlayer;
    private boolean continuePlaying = true;

    public Game() {
        this.createPlayers();
        this.start();
    }

    private void createPlayers() {
        System.out.println("Player 1, place your ships on the game field\n");
        this.player1 = new Player(this.scanner);
        this.passMove();
        System.out.println("Player 2, place your ships on the game field");
        this.currentPlayer = new Player(this.scanner);
        this.enemyPlayer = this.player1;
    }

    private void passMove() {
        System.out.print("\nPress Enter and pass the move to another player");
        String next = this.scanner.nextLine();
        while (!"".equals(next)) {
            next = this.scanner.nextLine();
        }
        System.out.println("\n".repeat(20));
        final Player temp = this.currentPlayer;
        this.currentPlayer = this.enemyPlayer;
        this.enemyPlayer = temp;
    }

    private void start() {
        this.passMove();
        while (this.continuePlaying) {
            this.printCurrentPlayerBoards();
            this.shoot();
        }
    }

    private void printCurrentPlayerBoards() {
        this.enemyPlayer.printFoggedBoard();
        System.out.println("-".repeat(21));
        this.currentPlayer.printBoard();
    }

    private void shoot() {
        final String whichPlayer = this.currentPlayer == this.player1 ? "Player 1" : "Player 2";
        System.out.printf("%n%s, it's your turn:%n", whichPlayer);

        String target = this.scanner.nextLine();

        while (this.enemyPlayer.isCoordinateInvalid(target)) {
            target = this.scanner.nextLine();
        }

        final int row = Helpers.charToIndex(target.charAt(0));
        final int column = Integer.parseInt(target.substring(1)) - 1;
        String message;

        switch (this.enemyPlayer.getCell(row, column)) {
            case 'O':
                this.enemyPlayer.setCell(row, column, 'X');
                message = this.enemyPlayer.parseHit(row, column);
                break;
            case '~':
                this.enemyPlayer.setCell(row, column, 'M');
                message = "You missed!";
                break;
            case 'X':
            case 'M':
            default:
                message = "You've already shot there.";
                break;
        }

        this.continuePlaying = !this.enemyPlayer.isEnd();

        if (!this.continuePlaying) {
            message = "You sank the last ship. You won. Congratulations!";
        }

        System.out.println(message);

        if (this.continuePlaying) {
            this.passMove();
        }
    }
}

final class Ship {
    final private int[][] coordinates;
    private int leftCells;

    Ship(final int length, final int[][] coordinates) {
        this.coordinates = coordinates;
        this.leftCells = length;
    }

    void markShot() {
        this.leftCells--;
    }

    boolean isSunk() {
        return this.leftCells == 0;
    }

    boolean isThere(final int[] coordinate) {
        boolean answer = false;

        for (final int[] shipCoordinate : this.coordinates) {
            if (shipCoordinate[0] == coordinate[0] && shipCoordinate[1] == coordinate[1]) {
                answer = true;
                break;
            }
        }

        return answer;
    }
}

final class Player {
    private final Scanner scanner;
    private char[][] board;
    private Ship aircraftCarrier;
    private Ship battleship;
    private Ship submarine;
    private Ship cruiser;
    private Ship destroyer;

    public Player(final Scanner scanner) {
        this.scanner = scanner;
        this.createBoard();
        this.printBoard();
        this.placeShips();
    }

    private void createBoard() {
        final char[][] board = new char[10][10];
        for (final char[] row : board) {
            Arrays.fill(row, '~');
        }
        this.board = board;
    }

    private void printBoard(final boolean fogged) {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");

        for (int index = 0; index < this.board.length; index++) {
            final String joined = CharBuffer.wrap(this.board[index]).chars()
                    .mapToObj(intValue -> String.valueOf((char) intValue))
                    .collect(Collectors.joining(" "));

            System.out.printf("%c %s%n", Helpers.indexToChar(index), fogged ? joined.replaceAll("O", "~") : joined);
        }
    }

    public void printBoard() {
        this.printBoard(false);
    }

    public void printFoggedBoard() {
        this.printBoard(true);
    }

    private void placeShips() {
        this.aircraftCarrier = this.placeShip("Aircraft Carrier", 5);
        this.battleship = this.placeShip("Battleship", 4);
        this.submarine = this.placeShip("Submarine", 3);
        this.cruiser = this.placeShip("Cruiser", 3);
        this.destroyer = this.placeShip("Destroyer", 2);
    }

    private Ship placeShip(final String name, final int cells) {
        System.out.printf("%nEnter the coordinates of the %s (%d cells):%n", name, cells);
        final int[][] coordinates = this.getValidCoordinates(cells);
        final int[][] listOfCoordinates = this.getListOfCoordinates(coordinates[0], coordinates[1]);
        this.putShipIntoBoard(listOfCoordinates);
        System.out.println();
        this.printBoard();
        return new Ship(cells, listOfCoordinates);
    }

    private int[][] getValidCoordinates(final int length) {
        final int[][] coordinates = new int[2][2];
        boolean areValid = false;

        while (!areValid) {
            final String[] stringCoordinates = scanner.nextLine().split(" ");
            if (stringCoordinates.length != 2) {
                this.printErrorMessage("Wrong input");
                continue;
            }

            final String start = stringCoordinates[0];
            final String end = stringCoordinates[1];
            if (this.isCoordinateInvalid(start) || this.isCoordinateInvalid(end)) {
                continue;
            }

            final int startRow = Helpers.charToIndex(start.charAt(0));
            final int endRow = Helpers.charToIndex(end.charAt(0));
            final int startColumn = Integer.parseInt(start.substring(1));
            final int endColumn = Integer.parseInt(end.substring(1));

            if (startRow != endRow && startColumn != endColumn) {
                this.printErrorMessage("Wrong ship location");
                continue;
            }

            final int[] valuesToCompare = startRow == endRow ? new int[] {endColumn, startColumn} : new int[] {endRow, startRow};
            if (Math.abs(valuesToCompare[0] - valuesToCompare[1]) != length - 1) {
                this.printErrorMessage("Wrong length of the ship");
                continue;
            }

            boolean isOccupied = false;
            if (startRow == endRow) {
                for (int index = 0; index < this.board[startRow].length; index++) {
                    if (index + 1 < Math.min(startColumn, endColumn) || index + 1 > Math.max(startColumn, endColumn)) {
                        continue;
                    }

                    if (this.board[startRow][index] != '~') {
                        isOccupied = true;
                        break;
                    }
                }
            } else {
                for (int index = 0; index < this.board.length; index++) {
                    if (index < Math.min(startRow, endRow) || index > Math.max(startRow, endRow)) {
                        continue;
                    }

                    if (this.board[index][startColumn - 1] != '~') {
                        isOccupied = true;
                        break;
                    }
                }
            }

            if (isOccupied) {
                this.printErrorMessage("Ship is already there");
                continue;
            }

            final int startCheckingRow = Math.max(Math.min(startRow, endRow) - 1, 0);
            final int endCheckingRow = Math.min(Math.max(startRow, endRow) + 1, 9);
            final int startCheckingColumn = Math.max(Math.min(startColumn, endColumn) - 2, 0);
            final int endCheckingColumn = Math.min(Math.max(startColumn, endColumn), 9);
            boolean isTooClose = false;

            for (int i = startCheckingRow; i < endCheckingRow + 1 && !isTooClose; i++) {
                for (int j = startCheckingColumn; j < endCheckingColumn + 1; j++) {
                    if (this.board[i][j] != '~') {
                        isTooClose = true;
                        break;
                    }
                }
            }

            if (isTooClose) {
                this.printErrorMessage("You placed it too close to another one");
                continue;
            }

            coordinates[0][0] = startRow;
            coordinates[0][1] = startColumn - 1;
            coordinates[1][0] = endRow;
            coordinates[1][1] = endColumn - 1;
            areValid = true;
        }

        return coordinates;
    }

    private void printErrorMessage(final String message) {
        System.out.printf("Error! %s! Try again:%n", message);
    }

    public boolean isCoordinateInvalid(final String coordinate) {
        if (coordinate.length() > 3 || coordinate.length() < 2) {
            this.printErrorMessage("Wrong coordinates format");
            return true;
        }

        final int row = Helpers.charToIndex(coordinate.charAt(0));
        if (row > 9 || row < 0 ) {
            this.printErrorMessage("First coordinates character should be A-J");
            return true;
        }

        try {
            final int column = Integer.parseInt(coordinate.substring(1));

            if (column > 10 || column < 1 ) {
                throw new NumberFormatException();
            }
        } catch(NumberFormatException numberFormatException) {
            this.printErrorMessage("Second part of coordinates should be a number from 1 to 10");
            return true;
        }

        return false;
    }

    private int[][] getListOfCoordinates(final int[] start, final int[] end) {
        final int[][] list;

        final int startRow = Math.min(start[0], end[0]);
        final int endRow = Math.max(start[0], end[0]);
        final int startColumn = Math.min(start[1], end[1]);
        final int endColumn = Math.max(start[1], end[1]);

        if (startRow == endRow) {
            final int length = endColumn - startColumn + 1;
            list = new int[length][2];

            for (int index = 0; index < length; index++) {
                list[index][0] = startRow;
                list[index][1] = startColumn + index;
            }
        } else {
            final int length = endRow - startRow + 1;
            list = new int[length][2];

            for (int index = 0; index < length; index++) {
                list[index][0] = startRow + index;
                list[index][1] = startColumn;
            }
        }

        return list;
    }

    private void putShipIntoBoard(final int[][] coordinates) {
        for (final int[] coordinate : coordinates) {
            this.board[coordinate[0]][coordinate[1]] = 'O';
        }
    }

    public char getCell(final int row, final int column) {
        return this.board[row][column];
    }

    public void setCell(final int row, final int column, final char symbol) {
        this.board[row][column] = symbol;
    }

    public boolean isEnd() {
        return this.aircraftCarrier.isSunk() && this.battleship.isSunk() && this.submarine.isSunk() && this.cruiser.isSunk() && this.destroyer.isSunk();
    }

    public String parseHit(final int row, final int column) {
        String message = "You hit a ship!";
        final int[] coordinate = new int[] {row, column};

        final Ship[] ships = new Ship[] {this.aircraftCarrier, this.battleship, this.submarine, this.cruiser, this.destroyer};

        for (final Ship ship : ships) {
            if (ship.isThere(coordinate)) {
                ship.markShot();

                if (ship.isSunk()) {
                    message = "You sank a ship!";
                }
            }
        }

        return message;
    }
}

final class Helpers {
    public static int charToIndex(final char character) {
        return character - 65;
    }

    public static char indexToChar(final int index) {
        return (char) (index + 65);
    }
}
