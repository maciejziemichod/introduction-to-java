package tictactoe;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        new TicTacToe();
    }
}

final class TicTacToe {
    private final char[][] table = new char[3][3];
    private TicTacToeState state;


    public TicTacToe () {
        Command command = InputHandler.getCommand();

        while (!command.isExit()) {
            final Player xPlayer = command.getXPlayer();
            final Player oPlayer = command.getOPlayer();
            final AI ai = new AI();
            this.createTable();
            this.printTable();
            this.state = TicTacToeState.X_PLAYING;
            this.updateState();

            while (this.shouldGameContinue()) {
                final int[] coordinates;
                if (this.state == TicTacToeState.X_PLAYING) {
                    if (xPlayer == Player.USER) {
                        coordinates = InputHandler.getValidCoordinates(this.table);
                    } else {
                        System.out.printf("Making move level \"%s\"\n", xPlayer.name().toLowerCase());
                        coordinates = ai.getCoordinates(this.table, 'X', xPlayer);
                    }
                } else {
                    if (oPlayer == Player.USER) {
                        coordinates =InputHandler.getValidCoordinates(this.table);
                    } else {
                        System.out.printf("Making move level \"%s\"\n", oPlayer.name().toLowerCase());
                        coordinates = ai.getCoordinates(this.table, 'O', oPlayer);
                    }
                }

                this.fillCell(coordinates);
                this.printTable();
                this.updateState();
                if (!this.shouldGameContinue()) {
                    this.printResult();

                    if (oPlayer == Player.USER || xPlayer == Player.USER) {
                        InputHandler.clearScanner();
                    }
                }
            }

            command = InputHandler.getCommand();
        }
    }

    private void createTable() {
        final String initialState = " ".repeat(9);

        for (int index = 0; index < initialState.length(); index++) {
            this.table[index / 3][index % 3] = initialState.charAt(index);
        }
    }

    private void printTable() {
        System.out.println("---------");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (j == 0) {
                    System.out.print("| ");
                }

                System.out.printf("%c ", this.table[i][j]);

                if (j == 2) {
                    System.out.println("|");
                }
            }
        }

        System.out.println("---------");
    }

    private void fillCell(final int[] coordinates) {
        this.table[coordinates[0] - 1][coordinates[1] - 1] = this.state == TicTacToeState.X_PLAYING ? 'X' : 'O';
    }

    private void updateState() {
        for (int[] winningPosition : TicTacToeUtils.WINNING_POSITIONS) {
            char cellAtFirst = TicTacToeUtils.getCellAtPosition(winningPosition[0], this.table);
            char cellAtSecond = TicTacToeUtils.getCellAtPosition(winningPosition[1], this.table);
            char cellAtThird = TicTacToeUtils.getCellAtPosition(winningPosition[2], this.table);

            if (cellAtFirst == cellAtSecond && cellAtSecond == cellAtThird && cellAtFirst != ' ') {
                this.state = cellAtFirst == 'X' ? TicTacToeState.X_WON : TicTacToeState.Y_WON;
                return;
            }
        }

        if (this.isTableFull()) {
            this.state = TicTacToeState.DRAW;
            return;
        }

        this.setTurn();
    }

    private void setTurn() {
        int emptyCells = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (this.table[i][j] == ' ') {
                    emptyCells++;
                }
            }
        }

        this.state = emptyCells % 2 == 0 ? TicTacToeState.O_PLAYING : TicTacToeState.X_PLAYING;
    }

    private boolean isTableFull() {
        boolean isFull = true;

        for (int i = 0; i < 3 && isFull; i++) {
            for (int j = 0; j < 3; j++) {
                if (this.table[i][j] == ' ') {
                    isFull = false;
                    break;
                }
            }
        }

        return isFull;
    }

    private void printResult() {
        String message;

        switch (this.state) {
            case X_WON:
                message = "X wins";
                break;
            case Y_WON:
                message = "O wins";
                break;
            case DRAW:
            default:
                message = "Draw";
                break;
        }

        System.out.println(message);
    }

    private boolean shouldGameContinue() {
        return this.state == TicTacToeState.O_PLAYING || this.state == TicTacToeState.X_PLAYING;
    }
}

final class InputHandler {
    private static final Scanner scanner = new Scanner(System.in);
    public static void clearScanner() {
        scanner.nextLine();
    }

    public static int[] getValidCoordinates(final char[][] table) {
        final int[] coordinates = new int[2];
        boolean areCoordinatesWrong = true;

        while(areCoordinatesWrong) {
            System.out.print("Enter the coordinates: ");
            try {
                final int firstCoordinate = scanner.nextInt();
                final int secondCoordinate = scanner.nextInt();

                if (firstCoordinate < 1 || firstCoordinate > 3 || secondCoordinate < 1 || secondCoordinate > 3) {
                    System.out.println("Coordinates should be from 1 to 3!");
                } else {
                    char value = table[firstCoordinate - 1][secondCoordinate - 1];
                    if (value == 'O' || value == 'X') {
                        System.out.println("This cell is occupied! Choose another one!");
                    } else {
                        coordinates[0] = firstCoordinate;
                        coordinates[1] = secondCoordinate;
                        areCoordinatesWrong = false;
                    }
                }
            } catch (Exception exception) {
                System.out.println("You should enter numbers!");
                scanner.nextLine();
            }
        }

        return coordinates;
    }

    public static Command getCommand() {
        boolean correctCommand = false;
        Command command = null;

        while (!correctCommand) {
            System.out.print("Input command: ");
            final String input = scanner.nextLine();
            final String[] parameters = input.split(" ");
            final int length = parameters.length;
            if (length == 0 || (!"start".equals(parameters[0]) && !"exit".equals(parameters[0]))) {
                System.out.println("Bad parameters!");
                continue;
            }

            if ("exit".equals(parameters[0])) {
                command = new Command(true);
                break;
            }

            if (length != 3) {
                System.out.println("Bad parameters!");
                continue;
            }

            if (Player.isNotOneOf(parameters[1]) || Player.isNotOneOf(parameters[2])) {
                System.out.println("Bad parameters!");
                continue;
            }

            command = new Command(false, Player.valueOf(parameters[1].toUpperCase()), Player.valueOf(parameters[2].toUpperCase()));
            correctCommand = true;
        }

        return command;
    }
}

final class Command {
    final private boolean exit;
    final private Player xPlayer;
    final private Player oPlayer;

    Command(final boolean exit, final Player xPlayer, final Player oPlayer) {
        this.exit = exit;
        this.xPlayer = xPlayer;
        this.oPlayer = oPlayer;
    }

    Command(final boolean exit) {
        this.exit = exit;
        this.xPlayer = Player.EASY;
        this.oPlayer = Player.EASY;
    }

    public boolean isExit() {
        return this.exit;
    }

    public Player getXPlayer() {
        return this.xPlayer;
    }

    public Player getOPlayer() {
        return this.oPlayer;
    }
}

enum Player {
    USER,
    EASY,
    MEDIUM,
    HARD;

    static boolean isNotOneOf(final String input) {
        boolean isOne = false;

        for (Player player : Player.values()) {
            if (player.name().toLowerCase().equals(input)) {
                isOne = true;
                break;
            }
        }

        return !isOne;
    }
}

final class AI {
    private final Random random = new Random();
    private char aiSign;
    private char enemySign;

    public int[] getCoordinates(final char[][] table, final char sign, final Player difficulty) {
        int[] coordinates = this.getRandomCoordinates(table);

        switch (difficulty) {
            case EASY:
            default:
                break;
            case MEDIUM:
                coordinates = this.getMediumLevelCoordinates(table, sign);
                break;
            case HARD:
                this.aiSign = sign;
                this.enemySign = sign == 'X' ? 'O' : 'X';
                coordinates = this.getHardLevelCoordinates(table, sign);
        }

        return coordinates;
    }

    private int[] getRandomCoordinates(final char[][] table) {
        final List<int[]> emptyCellsCoordinates = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (table[i][j] == ' ') {
                    emptyCellsCoordinates.add(new int[]{i + 1, j + 1});
                }
            }
        }

        return emptyCellsCoordinates.get(random.nextInt(emptyCellsCoordinates.size()));
    }

    private int[] getMediumLevelCoordinates(final char[][] table, final char sign) {
        int[] coordinates = this.getRandomCoordinates(table);

        final int[] winningCoordinates = this.getWinningMoveCoordinates(table, sign);

        return winningCoordinates == null ? coordinates : winningCoordinates;
    }

    private int[] getWinningMoveCoordinates(final char[][] table, final char sign) {
        int[] enemyWinningCoordinates = null;
        int[] winningCoordinates = null;

        for (final int[] winningPosition : TicTacToeUtils.WINNING_POSITIONS) {
            final char cellAtFirst = TicTacToeUtils.getCellAtPosition(winningPosition[0], table);
            final char cellAtSecond = TicTacToeUtils.getCellAtPosition(winningPosition[1], table);
            final char cellAtThird = TicTacToeUtils.getCellAtPosition(winningPosition[2], table);

            if (cellAtFirst == ' ') {
                if (cellAtSecond != ' ' && cellAtSecond == cellAtThird) {
                    if (cellAtSecond == sign) {
                        winningCoordinates = TicTacToeUtils.getCoordinatesFromPosition(winningPosition[0]);
                    } else {
                        enemyWinningCoordinates = TicTacToeUtils.getCoordinatesFromPosition(winningPosition[0]);
                    }
                }
            }

            if (cellAtSecond == ' ') {
                if (cellAtFirst != ' ' && cellAtFirst == cellAtThird) {
                    if (cellAtFirst == sign) {
                        winningCoordinates = TicTacToeUtils.getCoordinatesFromPosition(winningPosition[1]);
                    } else {
                        enemyWinningCoordinates = TicTacToeUtils.getCoordinatesFromPosition(winningPosition[1]);
                    }
                }
            }
            
            if (cellAtThird == ' ') {
                if (cellAtFirst != ' ' && cellAtFirst == cellAtSecond) {
                    if (cellAtSecond == sign) {
                        winningCoordinates = TicTacToeUtils.getCoordinatesFromPosition(winningPosition[2]);
                    } else {
                        enemyWinningCoordinates = TicTacToeUtils.getCoordinatesFromPosition(winningPosition[2]);
                    }
                }
            }
        }

        return winningCoordinates != null ? winningCoordinates : enemyWinningCoordinates;
    }


    private int[] getHardLevelCoordinates(final char[][] table, final char sign) {
        final int[] coordinates = this.minimax(table, sign).getCoordinates();
        coordinates[0] += 1;
        coordinates[1] += 1;

        return coordinates;
    }

    private Move minimax(final char[][] newTable, final char sign) {
        final int[][] emptyCells = this.getEmptyCellsCoordinates(newTable);

        if (isWin(newTable, this.aiSign)) {
            final Move move = new Move();
            move.setScore(10);
            return move;
        } else if (isWin(newTable, this.enemySign)) {
            final Move move = new Move();
            move.setScore(-10);
            return move;
        } else if (emptyCells.length == 0) {
            final Move move = new Move();
            move.setScore(0);
            return move;
        }

        final ArrayList<Move> moves = new ArrayList<>();

        for (final int[] emptyCell : emptyCells) {
            final Move move = new Move();
            move.setCoordinates(emptyCell);

            newTable[emptyCell[0]][emptyCell[1]] = sign;

            final Move result;
            if (sign == this.enemySign) {
                result = this.minimax(newTable, this.aiSign);
            } else {
                result = this.minimax(newTable, this.enemySign);
            }
            move.setScore(result.getScore());

            newTable[emptyCell[0]][emptyCell[1]] = ' ';

            moves.add(move);
        }

        int bestMove = 0;

        if (sign == this.aiSign) {
            int bestScore = -10000;

            for (final Move move : moves) {
                if (move.getScore() > bestScore) {
                    bestScore = move.getScore();
                    bestMove = moves.indexOf(move);
                }
            }
        } else {
            int bestScore = 10000;

            for (final Move move : moves) {
                if (move.getScore() < bestScore) {
                    bestScore = move.getScore();
                    bestMove = moves.indexOf(move);
                }
            }
        }

        return moves.get(bestMove);
    }

    private boolean isWin(final char[][] table, final char sign) {
        for (int[] winningPosition : TicTacToeUtils.WINNING_POSITIONS) {
            char cellAtFirst = TicTacToeUtils.getCellAtPosition(winningPosition[0], table);
            char cellAtSecond = TicTacToeUtils.getCellAtPosition(winningPosition[1], table);
            char cellAtThird = TicTacToeUtils.getCellAtPosition(winningPosition[2], table);

            if (cellAtFirst == cellAtSecond && cellAtSecond == cellAtThird && cellAtFirst == sign) {
                return true;
            }
        }

        return false;
    }

    private int[][] getEmptyCellsCoordinates(final char[][] table) {
        final ArrayList<int[]> emptyCells = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (table[i][j] == ' ') {
                    emptyCells.add(new int[] {i, j});
                }
            }
        }

        return emptyCells.toArray(new int[emptyCells.size()][2]);
    }
}

final class Move {
    private int[] coordinates;
    private int score;

    int[] getCoordinates() {
        return this.coordinates;
    }

    void setCoordinates(final int[] coordinates) {
        this.coordinates = coordinates.clone();
    }

    int getScore() {
        return this.score;
    }

    void setScore(final int score) {
        this.score = score;
    }
}

enum TicTacToeState {
    X_PLAYING,
    O_PLAYING,
    DRAW,
    X_WON,
    Y_WON
}

final class TicTacToeUtils {
    public static int[][] WINNING_POSITIONS = {
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8},
            {0, 3, 6},
            {1, 4, 7},
            {2, 5, 8},
            {0, 4, 8},
            {2, 4, 6}
    };

    public static char getCellAtPosition(final int position, final char[][] table) {
        return table[position / 3][position % 3];
    }

    public static int[] getCoordinatesFromPosition(final int position) {
        return new int[] {position / 3 + 1, position % 3 + 1};
    }
}
