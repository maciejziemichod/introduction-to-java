package machine;

import java.util.Scanner;

public final class CoffeeMachine {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        CoffeeMachineCore coffeeMachine = new CoffeeMachineCore();
        boolean shouldContinue;

        do {
           shouldContinue = coffeeMachine.process(getQuery(coffeeMachine.getMessage())).shouldContinue();
        } while (shouldContinue);
    }

    private static String getQuery(final String message) {
        System.out.println();
        System.out.println(message);
        return scanner.next();
    }
}

// Named this way to satisfy test requirements
final class CoffeeMachineCore {
    private int water = 400;
    private int milk = 540;
    private int coffee = 120;
    private int cups = 9;
    private int money = 550;
    private CoffeeMachineState state = CoffeeMachineState.CHOOSING_ACTION;
    private String query = "";
    private Boolean shouldContinue = true;
    private String message = "Write action (buy, fill, take, remaining, exit): ";
    private final Coffee espresso = new Coffee(250, 0, 16, 4);
    private final Coffee latte = new Coffee(350, 75, 20, 7);
    private final Coffee cappuccino = new Coffee(200, 100, 12, 6);

    public String getMessage() {
        return this.message;
    }

    private void setCorrectMessage() {
        switch (this.state) {
            case CHOOSING_ACTION:
                this.message = "Write action (buy, fill, take, remaining, exit): ";
                break;
            case CHOOSING_COFFEE:
                this.message = "What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: ";
                break;
            case FILLING_WATER:
                this.message = "Write how many ml of water you want to add: ";
                break;
            case FILLING_MILK:
                this.message = "Write how many ml of milk you want to add: ";
                break;
            case FILLING_COFFEE:
                this.message = "Write how many grams of coffee beans you want to add: ";
                break;
            case FILLING_CUPS:
                this.message = "Write how many disposable cups of coffee you want to add: ";
                break;
        }
    }

    public CoffeeMachineCore process(final String query) {
        this.query = query == null ? "" : query;

        switch (this.state) {
            case CHOOSING_ACTION:
                this.processChoosingAction();
                break;
            case CHOOSING_COFFEE:
                this.processChoosingCoffee();
                break;
            case FILLING_WATER:
            case FILLING_MILK:
            case FILLING_COFFEE:
            case FILLING_CUPS:
                this.processFillingSupplies();
                break;
        }

        this.setCorrectMessage();

        return this;
    }

    public boolean shouldContinue() {
        return this.shouldContinue;
    }

    private void processChoosingAction() {
        switch (this.query) {
            case "buy":
                this.state = CoffeeMachineState.CHOOSING_COFFEE;
                break;
            case "fill":
                this.state = CoffeeMachineState.FILLING_WATER;
                break;
            case "take":
                this.giveMoney();
                break;
            case "remaining":
                this.printState();
                break;
            case "exit":
                this.shouldContinue = false;
                break;
        }
    }

    private void giveMoney() {
        System.out.printf("I gave you $%d%n", this.money);
        this.money = 0;
    }

    private void printState() {
        System.out.println("\nThe coffee machine has:");
        System.out.printf("%d ml of water%n", this.water);
        System.out.printf("%d ml of milk%n", this.milk);
        System.out.printf("%d g of coffee beans%n", this.coffee);
        System.out.printf("%d disposable cups%n", this.cups);
        System.out.printf("$%d of money%n", this.money);
    }

    private void processFillingSupplies() {
        if (!this.query.matches("\\d+")) {
            return;
        }

        final int numericalQuery = Integer.parseInt(this.query);

        // They are in reversed order purposely, this makes the input not cause chain reaction
        switch (this.state) {
            case FILLING_CUPS:
                this.cups += numericalQuery;
                this.state = CoffeeMachineState.CHOOSING_ACTION;
                break;
            case FILLING_COFFEE:
                this.coffee += numericalQuery;
                this.state = CoffeeMachineState.FILLING_CUPS;
                break;
            case FILLING_MILK:
                this.milk += numericalQuery;
                this.state = CoffeeMachineState.FILLING_COFFEE;
                break;
            case FILLING_WATER:
                this.water += numericalQuery;
                this.state = CoffeeMachineState.FILLING_MILK;
                break;
        }
    }

    private void processChoosingCoffee() {
        this.state = CoffeeMachineState.CHOOSING_ACTION;

        switch (this.query) {
            case "1":
                this.sellCoffee(this.espresso);
                break;
            case "2":
                this.sellCoffee(this.latte);
                break;
            case "3":
                this.sellCoffee(this.cappuccino);
                break;
            case "back":
                break;
            default:
                this.state = CoffeeMachineState.CHOOSING_COFFEE;
        }
    }

    private void sellCoffee(final Coffee coffee) {
        if (makeCoffee(coffee)) {
            this.money += coffee.getPrice();
        }
    }

    private boolean makeCoffee(final Coffee coffee) {
        if (this.cups < 1) {
            this.printLackOfResource("cups");
            return false;
        }

        final int requiredWater = coffee.getWaterRequirement();
        final int requiredMilk = coffee.getMilkRequirement();
        final int requiredCoffee = coffee.getCoffeeRequirement();

        if (requiredWater > this.water) {
            this.printLackOfResource("water");
            return false;
        }
        if (requiredMilk > this.milk) {
            this.printLackOfResource("milk");
            return false;
        }
        if (requiredCoffee > this.coffee) {
            this.printLackOfResource("coffee beans");
            return false;
        }

        System.out.println("I have enough resources, making you a coffee!");
        this.water -= requiredWater;
        this.milk -= requiredMilk;
        this.coffee -= requiredCoffee;
        this.cups--;
        return true;
    }

    private void printLackOfResource(final String resource) {
        System.out.printf("Sorry, not enough %s!%n", resource);
    }
}

enum CoffeeMachineState {
    CHOOSING_ACTION, CHOOSING_COFFEE, FILLING_WATER, FILLING_MILK, FILLING_COFFEE, FILLING_CUPS
}

class Coffee {
    private final int water;
    private final int milk;
    private final int coffee;
    private final int price;

    public Coffee(final int water, final int milk, final int coffee, final int price) {
        this.water = water;
        this.milk = milk;
        this.coffee = coffee;
        this.price = price;
    }

    public final int getWaterRequirement() {
        return this.water;
    }

    public final int getMilkRequirement() {
        return this.milk;
    }

    public final int getCoffeeRequirement() {
        return this.coffee;
    }

    public final int getPrice() {
        return this.price;
    }
}
