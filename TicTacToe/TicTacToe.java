package tictactoe;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String[] symbolsArray = new String[9];
        Arrays.fill(symbolsArray, " ");

        printBoard(symbolsArray);

        String result = "Game not finished";
        int whoseMove = 0;

        while (result.equals("Game not finished")) {
            int[] coordinates = getCoordinates(symbolsArray);

            symbolsArray[convertCoordinatesToIndex(coordinates)] = whoseMove % 2 == 0 ? "X" : "O";
            whoseMove++;

            printBoard(symbolsArray);

            result = getResult(symbolsArray);
        }

        System.out.println(result);
    }

    private static void printBoard(String[] symbols) {
        int maxArrayLength = 9;

        System.out.println("---------");

        for (int index = 0; index < symbols.length && index < maxArrayLength; index++) {
            if (index % 3 == 0) {
                System.out.print("| ");
            }

            System.out.print(symbols[index] + " ");

            if (index % 3 == 2) {
                System.out.println("|");
            }
        }

        System.out.println("---------");
    }

    private static int[] getCoordinates(String[] symbols) {
        System.out.print("Enter the coordinates: ");
        int yCoordinate;
        int xCoordinate;

        while (true) {
            try {
                yCoordinate = scanner.nextInt();
                xCoordinate = scanner.nextInt();
                if (
                    yCoordinate < 4
                    && yCoordinate > 0
                    && xCoordinate < 4
                    && xCoordinate > 0
                ) {
                    if (isCellOccupied(new int[] {yCoordinate, xCoordinate}, symbols)) {
                        System.out.println("This cell is occupied! Choose another one!");
                        System.out.print("Enter the coordinates: ");
                        continue;
                    }

                    break;
                }
                System.out.println("Coordinates should be from 1 to 3!");
                System.out.print("Enter the coordinates: ");
            } catch (Exception exception) {
                System.out.println("You should enter numbers!");
                System.out.print("Enter the coordinates: ");
                // Clear scanner input
                scanner.nextLine();
            }
        }

        return new int[]{yCoordinate, xCoordinate};
    }

    private static int convertCoordinatesToIndex(int[] coordinates) {
        return (coordinates[0] - 1) * 3 + coordinates[1] - 1;
    }

    private static boolean isCellOccupied(int[] coordinates, String[] symbols) {
        boolean isOccupied = false;
        int index = convertCoordinatesToIndex(coordinates);

        if (symbols[index].equals("X") || symbols[index].equals("O")) {
            isOccupied = true;
        }

        return isOccupied;
    }

    private static String getResult(String[] symbols) {
        int xOccurrences = 0;
        int oOccurrences = 0;

        for (String symbol : symbols) {
            if (symbol.equals("X")) {
                xOccurrences++;
            }

            if (symbol.equals("O")) {
                oOccurrences++;
            }
        }

        if (Math.abs(xOccurrences - oOccurrences) > 1 || (symbols[0].equals(symbols[2]) && symbols[0].equals(symbols[4]) && symbols[0].equals(symbols[6]) && symbols[0].equals(symbols[8]) && (symbols[0].equals("X") || symbols[0].equals("O")))) {
            return "Impossible";
        }

        int[][] winningPositions = {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8},
                {0, 3, 6},
                {1, 4, 7},
                {2, 5, 8},
                {0, 4, 8},
                {2, 4, 6},
        };
        boolean didXWon = false;
        boolean didOWon = false;

        for(int[] position : winningPositions) {
            if (!symbols[position[0]].equals("O") && !symbols[position[0]].equals("X")) {
                continue;
            }

            boolean isWin = symbols[position[0]].equals(symbols[position[1]]) && symbols[position[0]].equals(symbols[position[2]]);

            if (!isWin) {
                continue;
            }

            switch (symbols[position[0]]) {
                case "O":
                    didOWon = true;
                    break;
                case "X":
                    didXWon = true;
                    break;
                default:
                    break;
            }
        }

        if (didOWon && didXWon) {
            return "Impossible";
        } else if (didOWon) {
            return "O wins";
        } else if (didXWon) {
            return "X wins";
        } else if (xOccurrences + oOccurrences == 9) {
            return "Draw";
        } else {
            return "Game not finished";
        }
    }
}
