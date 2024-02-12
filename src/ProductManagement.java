import java.util.List;

public interface ProductManagement {
    void sendProductsToSupermarket(List<Product> products, String destinationSupermarket);
    void addProduct(Product product);
    void removeProduct(Product product);
    List<Product> getProducts();
}