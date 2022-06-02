package ch.epfl.sdp.healthplay.scan

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.Exception

class ProductManagerTest {
    @Test
    fun productManager() {
        assertFalse(ProductManager.getProduct("asides").join().isPresent)
    }

    @Test
    fun correctProductManager() {
        assertTrue(ProductManager.getProduct("737628064502").join().isPresent)
    }

    @Test
    fun withExceptionProductManager() {
        assertFalse(ProductManager.getProductInner { throw Exception() }.join().isPresent)
    }
}