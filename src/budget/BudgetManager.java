package budget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class BudgetManager {
    private final Scanner scanner = new Scanner(System.in);
    private final List<Purchase> purchases = new ArrayList<>();
    private double balance = 0;
    private final Map<Integer, String> purchaseType = Map.of(
            1, "Food",
            2, "Clothes",
            3, "Entertainment",
            4, "Other",
            5, "All"
    );

    public void run() {
        while (true) {
            System.out.println("Choose your action:\n" +
                    "1) Add income\n" +
                    "2) Add purchase\n" +
                    "3) Show list of purchases\n" +
                    "4) Balance\n" +
                    "5) Save\n" +
                    "6) Load\n" +
                    "7) Analyze (Sort)\n" +
                    "0) Exit");

            int response = Integer.parseInt(scanner.nextLine());
            System.out.println();
            switch (response) {
                case 1:
                    addIncome();
                    break;
                case 2:
                    addPurchase();
                    break;
                case 3:
                    printPurchases();
                    break;
                case 4:
                    printBalance();
                    break;
                case 5:
                    savePurchases();
                    break;
                case 6:
                    loadPurchases();
                    break;
                case 7:
                    analyze();
                    break;
                case 0:
                    exit();
                    return;
            }
            //System.out.println();
        }
    }

    public void addIncome() {
        System.out.println("Enter income:");
        balance += Double.parseDouble(scanner.nextLine());
        System.out.println("Income was added!");
        System.out.println();
    }

    public void addPurchase() {
        while (true) {
            System.out.println("Choose the type of purchase\n" +
                    "1) Food\n" +
                    "2) Clothes\n" +
                    "3) Entertainment\n" +
                    "4) Other\n" +
                    "5) Back");
            int type = Integer.parseInt(scanner.nextLine());
            System.out.println();

            if (type == 5) {
                System.out.println();
                return;
            }

            System.out.println("Enter purchase name:");
            String name = scanner.nextLine();
            System.out.println("Enter its price:");
            double price = Double.parseDouble(scanner.nextLine());
            purchases.add(new Purchase(type, name, price));
            balance -= price;
            System.out.println("Purchase was added!");
            System.out.println();
        }

    }

    public void printPurchases() {
        if (purchases.isEmpty()) {
            System.out.println("The purchase list is empty!");
            System.out.println();
            return;
        }
        while (true) {
            System.out.println("Choose the type of purchases\n" +
                    "1) Food\n" +
                    "2) Clothes\n" +
                    "3) Entertainment\n" +
                    "4) Other\n" +
                    "5) All\n" +
                    "6) Back");
            int type = Integer.parseInt(scanner.nextLine());

            if (type == 6) {
                System.out.println();
                return;
            }

            System.out.println();
            List<Purchase> toPrint;
            if (type == 5) {
                toPrint = purchases;
            } else {
                toPrint = purchases.stream().filter(x -> x.getType() == type).collect(Collectors.toList());
            }

            System.out.println(purchaseType.get(type) + ":");
            if (toPrint.isEmpty()) {
                System.out.println("The purchase is empty!");
                System.out.println();
                continue;
            }
            double totalPurchase = toPrint.stream().map(Purchase::getPrice).reduce(0.0, Double::sum);
            toPrint.forEach(System.out::println);
            System.out.println("Total sum: $" + String.format("%.2f", totalPurchase));
            System.out.println();
            System.out.println();
        }
    }

    public void printBalance() {
        if (balance < 0.0) {
            balance = 0.0;
        }
        System.out.println("Balance: $" + String.format("%.2f", balance));
        System.out.println();
    }

    public void savePurchases() {
        try (PrintWriter printWriter = new PrintWriter("purchases.txt")) {
            printWriter.println("Balance\t" + balance);
            purchases.forEach(x -> printWriter.println(x.toStringInFile()));
        } catch (FileNotFoundException ignored) {}
        System.out.println("Purchases were saved!");
        System.out.println();
    }

    public void loadPurchases() {
        try (Scanner scanner = new Scanner(new File("purchases.txt"))) {
            while (scanner.hasNextLine()) {
                String[] entry = scanner.nextLine().split("\\t");
                if ("Balance".equals(entry[0])) {
                    balance = Double.parseDouble(entry[1]);
                    continue;
                }
                int type = Integer.parseInt(entry[0]);
                String name = entry[1];
                double price = Double.parseDouble(entry[2]);
                purchases.add(new Purchase(type, name, price));
            }
        } catch (FileNotFoundException ignored) {}
        System.out.println("Purchases were loaded!");
        System.out.println();
    }

    public void analyze() {
        while (true) {
            System.out.println("How do you want to sort?\n" +
                    "1) Sort all purchases\n" +
                    "2) Sort by type\n" +
                    "3) Sort certain type\n" +
                    "4) Back");
            int response = Integer.parseInt(scanner.nextLine());

            System.out.println();
            switch (response) {
                case 1:
                    sortAll();
                    break;
                case 2:
                    sortByType();
                    break;
                case 3:
                    System.out.println("Choose the type of purchase\n" +
                            "1) Food\n" +
                            "2) Clothes\n" +
                            "3) Entertainment\n" +
                            "4) Other");
                    int type = Integer.parseInt(scanner.nextLine());

                    System.out.println();
                    sortAType(type);
                    break;
                case 4:
                    return;
            }
        }
    }

    public void sortAll() {
        purchases.sort(Comparator.comparing(Purchase::getPrice).reversed());
        if (purchases.isEmpty()) {
            System.out.println("The purchase list is empty!");
            System.out.println();
            return;
        }
        double totalPurchase = purchases.stream().map(Purchase::getPrice).reduce(0.0, Double::sum);
        System.out.println("All:");
        purchases.forEach(System.out::println);
        System.out.println("Total sum: $" + String.format("%.2f", totalPurchase));
        System.out.println();
    }

    public void sortByType() {
        Map<Integer, Double> totalEachType = new HashMap<>();
        totalEachType.put(1, 0.0);
        totalEachType.put(2, 0.0);
        totalEachType.put(3, 0.0);
        totalEachType.put(4, 0.0);
        purchases.forEach(x -> totalEachType.merge(x.getType(), x.getPrice(), Double::sum));
        double totalPurchase = purchases.stream().map(Purchase::getPrice).reduce(0.0, Double::sum);
        System.out.println("Types:");
        totalEachType.entrySet().stream().sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .forEach(x -> System.out.println(purchaseType.get(x.getKey()) + " - $" +
                                String.format("%.2f", x.getValue())));
        System.out.println("Total sum: $" + String.format("%.2f", totalPurchase));
        System.out.println();
    }

    public void sortAType(int type) {
        List<Purchase> toPrint = purchases.stream().filter(x -> x.getType() == type)
                .sorted(Comparator.comparing(Purchase::getPrice).reversed()).collect(Collectors.toList());
        if (toPrint.isEmpty()) {
            System.out.println("The purchase list is empty!");
            System.out.println();
            return;
        }
        double totalPurchase = toPrint.stream().map(Purchase::getPrice).reduce(0.0, Double::sum);
        System.out.println(purchaseType.get(type) + ":");
        toPrint.forEach(System.out::println);
        System.out.println("Total sum: $" + String.format("%.2f", totalPurchase));
        System.out.println();
    }

    public void exit() {
        System.out.println("Bye!");
    }
}
