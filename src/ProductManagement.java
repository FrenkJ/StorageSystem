import java.util.List;

public interface ProductManagement {
    void sendProductsToSupermarket(List<Product> products, String destinationSupermarket);

    void addProduct(Product product);

    List<Product> getProducts();

    // Additional product management methods...
}