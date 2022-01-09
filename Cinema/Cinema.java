package cinema;

import java.util.Scanner;

public final class Cinema {
    private static final Scanner scanner = new Scanner(System.in);
    private static int purchasedTickets = 0;
    private static int currentIncome = 0;

    public static void main(final String[] args) {
        final int rows = getRowsNumber();
        final int seats = getSeatsNumber();
        final char[][] scheme = createScheme(rows, seats);

        int menuChoice = getMenuChoice();

        while (menuChoice != 0) {
            performAction(menuChoice, scheme, rows, seats);
            menuChoice = getMenuChoice();
        }
    }

    private static int getRowsNumber() {
        System.out.println("Enter the number of rows:");

        return scanner.nextInt();
    }

    private static int getSeatsNumber() {
        System.out.println("Enter the number of seats in each row:");

        return scanner.nextInt();
    }

    private static char[][] createScheme(final int rows, final int seats) {
        final char[][] scheme = new char[rows + 1][seats + 1];

        for (int i = 0; i <= rows; i++) {
            for (int j = 0; j <= seats; j++) {
                if (i == 0 && j == 0) {
                    scheme[0][0] = ' ';
                } else if (i == 0) {
                    scheme[0][j] = (char) (j + '0');
                } else if (j == 0) {
                    scheme[i][0] = (char) (i + '0');
                } else {
                    scheme[i][j] = 'S';
                }
            }
        }

        return scheme;
    }

    private static int getMenuChoice() {
        System.out.println("\n1. Show the seats\n2. Buy a ticket\n3. Statistics\n0. Exit");
        return scanner.nextInt();
    }

    private static void performAction(final int choice, final char[][] scheme, final int rows, final int seats) {
        switch (choice) {
            case 1:
                printScheme(scheme);
                break;
            case 2:
                buyTicket(scheme, rows, seats);
                break;
            case 3:
                showStatistics(rows, seats);
                break;
        }
    }

    private static void printScheme(final char[][] scheme) {
        System.out.println("\nCinema:");

        for (final char[] row : scheme) {
            for (final char character: row) {
                System.out.printf("%c ", character);
            }
            System.out.println();
        }
    }

    private static void buyTicket(final char[][] scheme, final int rows, final int seats) {
        final int[] coordinates = getProperCoordinates(scheme, rows, seats);
        final int ticketPrice = calculateTicketPrice(coordinates, rows, seats);
        purchasedTickets++;
        currentIncome += ticketPrice;
        printTicketPrice(ticketPrice);
        markChosenSeat(coordinates, scheme);
    }

    private static void showStatistics(final int rows, final int seats) {
        printNumberOfPurchasedTickets();
        printPercentageOfPurchasedTickets(rows * seats);
        printCurrentIncome();
        printTotalIncome(rows, seats);
    }

    private static int[] getProperCoordinates(final char[][] scheme, final int rows, final int seats) {
        int[] coordinates = getCoordinates();

        while (!validateCoordinates(coordinates, scheme, rows, seats)) {
            coordinates = getCoordinates();
        }

        return coordinates;
    }

    private static boolean validateCoordinates(final int[] coordinates, final char[][] scheme, final int rows, final int seats) {
        if (
            coordinates[0] < 1
            || coordinates[1] < 1
            || coordinates[0] > rows
            || coordinates[1] > seats
        ) {
            System.out.println("\nWrong input!");
            return false;
        }

        if (scheme[coordinates[0]][coordinates[1]] == 'B') {
            System.out.println("\nThat ticket has already been purchased!");
            return false;
        }

        return true;
    }

    private static int[] getCoordinates() {
        System.out.println("\nEnter a row number:");
        int row = scanner.nextInt();
        System.out.println("Enter a seat number in that row:");
        int seat = scanner.nextInt();

        return new int[] {row, seat};
    }

    private static int calculateTicketPrice(final int[] coordinates, final int rows, final int seats) {
        if (rows * seats <= 60) {
            return 10;
        }

        return coordinates[0] <= rows / 2 ? 10 : 8;
    }

    private static void printTicketPrice(final int price) {
        System.out.printf("Ticket price: $%d%n", price);
    }

    private static void markChosenSeat(final int[] coordinates, final char[][] scheme) {
        scheme[coordinates[0]][coordinates[1]] = 'B';
    }

    private static void printNumberOfPurchasedTickets() {
        System.out.printf("%nNumber of purchased tickets: %d%n", purchasedTickets);
    }

    private static void printPercentageOfPurchasedTickets(int numberOfSeats) {
        final double percentage = ((double) purchasedTickets / (double) numberOfSeats) * 100;
        System.out.printf("Percentage: %.2f%%%n", percentage);
    }

    private static void printCurrentIncome() {
        System.out.printf("Current income: $%d%n", currentIncome);
    }

    private static int calculateTotalIncome(final int rows, final int seats) {
        final int numberOfSeats = rows * seats;

        if (numberOfSeats <= 60) {
            return numberOfSeats * 10;
        }

        final int frontHalfRowsNumber = rows / 2;

        return frontHalfRowsNumber * seats * 10 + (rows - frontHalfRowsNumber) * seats * 8;
    }

    private static void printTotalIncome(final int rows, final int seats) {
        System.out.printf("Total income: $%d%n", calculateTotalIncome(rows, seats));
    }
}
