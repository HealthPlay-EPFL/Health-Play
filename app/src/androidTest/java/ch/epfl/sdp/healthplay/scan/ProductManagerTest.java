package ch.epfl.sdp.healthplay.scan;

import static org.junit.Assert.*;

import androidx.annotation.VisibleForTesting;

import org.junit.Test;

import java.io.IOException;

import ch.epfl.sdp.healthplay.model.ProductInfoClient;

public class ProductManagerTest {

    @Test
    public void getProductManager() {
        assertFalse(ProductManager.getProduct("asdfasdf").join().isPresent());
    }

    @Test
    public void getCorrectProductManager() {
        assertTrue(ProductManager.getProduct("737628064502").join().isPresent());
    }

    @Test
    public void withExceptionProductManager() {
        assertFalse(ProductManager.getProductInner(() -> {
            throw new Exception();
        }).join().isPresent());
    }
}