public class Detergent implements Product {
    private String name;
    private double price;
    private int amount;

    public Detergent(String name, double price, int amount) {
        this.name = name;
        this.price = price;
        this.amount = amount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void updateAmount(int newAmount) {

    }

    @Override
    public void updatePrice(double newPrice) {

    }

    @Override
    public Product createNewInstance(String name, double price, int amount) {
        return new Detergent(name, price, amount);
    }

    // You might want to override equals and hashCode methods as well
}