package ch.epfl.sdp.healthplay.scan;

import androidx.annotation.VisibleForTesting;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.healthplay.model.Product;
import ch.epfl.sdp.healthplay.model.ProductInfoClient;

public final class ProductManager {

    // No need to instantiate this class
    private ProductManager() {
    }

    // This function is used for testing
    @VisibleForTesting
    static CompletableFuture<Optional<Product>> getProductInner(Callable<ProductInfoClient> c) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ProductInfoClient client = c.call();
                String productString = client.getInfo();
                return Product.of(productString);
            } catch (Exception e) {
                return Optional.empty();
            }
        });
    }

    /**
     * Get the product, if it exists
     *
     * @param barcode the barcode of the product
     * @return a completable future that contains an option of a product,
     * this option contains the product if it is not empty
     */
    public static CompletableFuture<Optional<Product>> getProduct(String barcode) {
        return getProductInner(() -> new ProductInfoClient(barcode));
    }
}