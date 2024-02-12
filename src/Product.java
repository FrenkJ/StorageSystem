
import java.util.ArrayList;
import java.util.List;

public interface Product {
    String getName();
    double getPrice();
    int getAmount();
    void updateAmount(int newAmount);

    void updatePrice(double newPrice);

    Product createNewInstance(String name, double price, int amount);
}















