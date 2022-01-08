package budget;

public class Purchase {
    private int type;
    private String name;
    private double price;

    public Purchase(int type, String name, double price) {
        this.type = type;
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getType() {
        return type;
    }

    public String toStringInFile() {
        return type + "\t" + name + "\t" + price;
    }

    @Override
    public String toString() {
        return name + " $" + String.format("%.2f", price);
    }
}
