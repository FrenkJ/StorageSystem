import java.util.ArrayList;
import java.util.List;

class StorageManager implements ProductManagement {
    private List<Product> products;

    public StorageManager() {
        this.products = new ArrayList<>();
        // Initialize with some products

    }

    @Override
    public void sendProductsToSupermarket(List<Product> products, String destinationSupermarket) {

    }

    @Override
    public void addProduct(Product product) {
        products.add(product);
    }

    @Override
    public void removeProduct(Product product) {
        products.remove(product);
    }

    @Override
    public List<Product> getProducts() {
        return null;
    }
}
