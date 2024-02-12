public class ConcreteProduct implements Product {
    private String name;
    private double price;
    private int amount;

    public ConcreteProduct(String name, double price, int amount) {
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
    public Product createNewInstance(String name, double price, int amount) {
        return new ConcreteProduct(name, price, amount);
    }

    @Override
    public void updateAmount(int newAmount) {
        this.amount += newAmount;
    }

    @Override
    public void updatePrice(double newPrice) {

    }
}
