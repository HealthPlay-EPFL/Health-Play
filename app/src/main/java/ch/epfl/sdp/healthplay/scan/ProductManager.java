package ch.epfl.sdp.healthplay.scan;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.healthplay.model.Product;
import ch.epfl.sdp.healthplay.model.ProductInfoClient;

public final class ProductManager {

    // No need to instantiate this class
    private ProductManager() {
    }

    /**
     * Get the product, if it exists
     *
     * @param barcode the barcode of the product
     * @return a completable future that contains an option of a product,
     * this option contains the product if it is not empty
     */
    public static CompletableFuture<Optional<Product>> getProduct(String barcode) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ProductInfoClient client = new ProductInfoClient(barcode);
                String productString = client.getInfo();
                return Product.of(productString);
            } catch (IOException e) {
                return Optional.empty();
            }
        });
    }
}