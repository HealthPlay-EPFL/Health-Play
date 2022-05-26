package ch.epfl.sdp.healthplay.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Optional;

public class ProductTest {
    private final static String CORRECT_JSON = "{\n" +
            "  \"code\":\"0737628064502\",\n" +
            "  \"product\":{\n" +
            "  \"generic_name\":\"Rice Noodles\",\n" +
            "  \"product_name\":\"Thai peanut noodle kit includes stir-fry rice noodles & thai peanut seasoning\",\n" +
            "  \"nutriments\":{\n" +
            "  \"energy-kcal\":385,\n" +
            "  }}}";

    private final static String N_A_N_1 = "{\n" +
            "  \"code\":\"0737628064502\",\n" +
            "  \"product\":{\n" +
            "  \"generic_name\":\"Rice Noodles\",\n" +
            "  \"product_name\":\"Thai peanut noodle kit includes stir-fry rice noodles & thai peanut seasoning\",\n" +
            "  \"nova_groups\":1,\n" +
            "  \"nutriscore_grade\":\"a\",\n" +
            "  \"nutriments\":{\n" +
            "  \"energy-kcal\":385,\n" +
            "  }}}";

    private final static String N_B_N_2 = "{\n" +
            "  \"code\":\"0737628064502\",\n" +
            "  \"product\":{\n" +
            "  \"generic_name\":\"Rice Noodles\",\n" +
            "  \"product_name\":\"Thai peanut noodle kit includes stir-fry rice noodles & thai peanut seasoning\",\n" +
            "  \"nova_groups\":2,\n" +
            "  \"nutriscore_grade\":\"b\",\n" +
            "  \"nutriments\":{\n" +
            "  \"energy-kcal\":385,\n" +
            "  }}}";

    private final static String N_C_N_3 = "{\n" +
            "  \"code\":\"0737628064502\",\n" +
            "  \"product\":{\n" +
            "  \"generic_name\":\"Rice Noodles\",\n" +
            "  \"product_name\":\"Thai peanut noodle kit includes stir-fry rice noodles & thai peanut seasoning\",\n" +
            "  \"nova_groups\":3,\n" +
            "  \"nutriscore_grade\":\"c\",\n" +
            "  \"nutriments\":{\n" +
            "  \"energy-kcal\":385,\n" +
            "  }}}";

    private final static String N_D_N_4 = "{\n" +
            "  \"code\":\"0737628064502\",\n" +
            "  \"product\":{\n" +
            "  \"generic_name\":\"Rice Noodles\",\n" +
            "  \"product_name\":\"Thai peanut noodle kit includes stir-fry rice noodles & thai peanut seasoning\",\n" +
            "  \"nova_groups\":4,\n" +
            "  \"nutriscore_grade\":\"d\",\n" +
            "  \"nutriments\":{\n" +
            "  \"energy-kcal\":385,\n" +
            "  }}}";

    private final static String N_E_N_U = "{\n" +
            "  \"code\":\"0737628064502\",\n" +
            "  \"product\":{\n" +
            "  \"generic_name\":\"Rice Noodles\",\n" +
            "  \"product_name\":\"Thai peanut noodle kit includes stir-fry rice noodles & thai peanut seasoning\",\n" +
            "  \"nova_groups\":5,\n" +
            "  \"nutriscore_grade\":\"e\",\n" +
            "  \"nutriments\":{\n" +
            "  \"energy-kcal\":385,\n" +
            "  }}}";

    private final static String N_U_N_U = "{\n" +
            "  \"code\":\"0737628064502\",\n" +
            "  \"product\":{\n" +
            "  \"generic_name\":\"Rice Noodles\",\n" +
            "  \"product_name\":\"Thai peanut noodle kit includes stir-fry rice noodles & thai peanut seasoning\",\n" +
            "  \"nova_groups\":6,\n" +
            "  \"nutriscore_grade\":\"f\",\n" +
            "  \"nutriments\":{\n" +
            "  \"energy-kcal\":385,\n" +
            "  }}}";

    private final static String EMPTY_JSON = "{\n" +
            "  \"code\":\"0737628064502\",\n" +
            "  \"product\":{\n" +
            "  \"generic_name\": null,\n" +
            "  \"product_name\":null,\n" +
            "  \"nutriments\":{\n" +
            "  \"energy-kcal\":null,\n" +
            "  }}}";

    @Test
    public void testNotCorrectlyFormattedJSONTIsNotEmpty() {
        assertFalse(Product.of("unformatted: null").isPresent());
    }

    @Test
    public void testCorrectName() {
        Optional<Product> optionalProduct = Product.of(CORRECT_JSON);
        assertTrue(optionalProduct.isPresent());
        Product p = optionalProduct.get();
        assertEquals(
                "Thai peanut noodle kit includes stir-fry rice noodles & thai peanut seasoning",
                p.getName());
    }

    @Test
    public void testEmptyName() {
        Optional<Product> optionalProduct = Product.of(EMPTY_JSON);
        assertTrue(optionalProduct.isPresent());
        Product p = optionalProduct.get();
        assertEquals(Product.UNKNOWN_NAME, p.getName());
    }

    @Test
    public void testCorrectKCal() {
        Optional<Product> optionalProduct = Product.of(CORRECT_JSON);
        assertTrue(optionalProduct.isPresent());
        Product p = optionalProduct.get();
        assertEquals("Rice Noodles", p.getGenericName());
    }

    @Test
    public void testEmptyKCal() {
        Optional<Product> optionalProduct = Product.of(EMPTY_JSON);
        assertTrue(optionalProduct.isPresent());
        Product p = optionalProduct.get();
        assertEquals(Product.UNKNOWN_NAME, p.getGenericName());
    }

    @Test
    public void testCorrectGenericName() {
        Optional<Product> optionalProduct = Product.of(CORRECT_JSON);
        assertTrue(optionalProduct.isPresent());
        Product p = optionalProduct.get();
        assertEquals(385, p.getKCalEnergy());
    }

    @Test
    public void testEmptyGenericName() {
        Optional<Product> optionalProduct = Product.of(EMPTY_JSON);
        assertTrue(optionalProduct.isPresent());
        Product p = optionalProduct.get();
        assertEquals(Product.UNKNOWN_VALUE, p.getKCalEnergy());
    }

    @Test
    public void testNutrimentName() {
        Product.Nutriments nutriment = Product.Nutriments.ALCOHOL;
        assertEquals(nutriment.getName(), "alcohol");
    }

    @Test
    public void testGet100Grams() {
        Optional<Product> optionalProduct = Product.of(CORRECT_JSON);
        assertTrue(optionalProduct.isPresent());
        Product p = optionalProduct.get();
        //"energy-kcal"
        for (Product.Nutriments nutriment: Product.Nutriments.values()) {
            assertEquals(Product.UNKNOWN_VALUE, p.getNutriment100Grams(nutriment), 0.1);
        }
    }

    @Test
    public void testGetServing() {
        Optional<Product> optionalProduct = Product.of(CORRECT_JSON);
        assertTrue(optionalProduct.isPresent());
        Product p = optionalProduct.get();
        assertEquals(Product.UNKNOWN_VALUE, p.getNutrimentServing(Product.Nutriments.ENERGY_KCAL),
                0.1);
    }

    @Test
    public void testGetUnit() {
        Optional<Product> optionalProduct = Product.of(CORRECT_JSON);
        assertTrue(optionalProduct.isPresent());
        Product p = optionalProduct.get();
        assertEquals(Product.UNKNOWN_NAME, p.getNutrimentUnit(Product.Nutriments.ENERGY_KCAL));
    }

    @Test
    public void testGetValue() {
        Optional<Product> optionalProduct = Product.of(CORRECT_JSON);
        assertTrue(optionalProduct.isPresent());
        Product p = optionalProduct.get();
        assertEquals(Product.UNKNOWN_VALUE, p.getNutrimentValue(Product.Nutriments.ENERGY_KCAL),
                0.1);
    }

    @Test
    public void testGet() {
        Optional<Product> optionalProduct = Product.of(CORRECT_JSON);
        assertTrue(optionalProduct.isPresent());
        Product p = optionalProduct.get();
        assertEquals(385, p.getNutriment(Product.Nutriments.ENERGY_KCAL),
                0.1);
        assertEquals(Product.UNKNOWN_VALUE, p.getNutriment(Product.Nutriments.SALT),
                0.1);
    }

    @Test
    public void testNutriAndNova() {
        Optional<Product> optionalProduct = Product.of(N_A_N_1);
        assertTrue(optionalProduct.isPresent());
        Product p = optionalProduct.get();
        p.getNutriscore();
        p.getNova();
        p.getNutriscore().getScore();

        optionalProduct = Product.of(N_B_N_2);
        assertTrue(optionalProduct.isPresent());
        p = optionalProduct.get();
        p.getNutriscore();
        p.getNova();
        p.getNutriscore().getScore();

        optionalProduct = Product.of(N_C_N_3);
        assertTrue(optionalProduct.isPresent());
        p = optionalProduct.get();
        p.getNutriscore();
        p.getNova();
        p.getNutriscore().getScore();

        optionalProduct = Product.of(N_D_N_4);
        assertTrue(optionalProduct.isPresent());
        p = optionalProduct.get();
        p.getNutriscore();
        p.getNova();
        p.getNutriscore().getScore();

        optionalProduct = Product.of(N_E_N_U);
        assertTrue(optionalProduct.isPresent());
        p = optionalProduct.get();
        p.getNutriscore();
        p.getNova();
        p.getNutriscore().getScore();

        optionalProduct = Product.of(N_U_N_U);
        assertTrue(optionalProduct.isPresent());
        p = optionalProduct.get();
        p.getNutriscore();
        p.getNova();
        p.getNutriscore().getScore();
    }
}
