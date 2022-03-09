package ch.epfl.sdp.healthplay.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

/**
 * Class representing a product
 */
public final class Product {
    /**
     * Value returned when some product info are not available.
     */
    public final static String UNKNOWN_NAME = "Unknown";
    /**
     * Value returned when some product info of positive integers ar not available.
     * For example, if the energy of a product is not known,
     * getKCal() will return -1.
     */
    public final static int UNKNOWN_VALUE = -1;

    private final static String GEN_NAME = "generic_name";
    private final static String FULL_NAME = "product_name";

    private static final String NUTRIMENTS = "nutriments";
    private static final String ENERGY_KCAL = "energy-kcal";

    // JSON object containing the information of the product
    private final JSONObject product;

    private Product(String json) throws JSONException {
        // This also checks that the JSON contains the "product" name.
        product = new JSONObject(json).getJSONObject("product");
    }

    /**
     * Given a JSON string, construct a product out of it.
     *
     * @param json the JSON formatted string for a product
     * @return the product
     */
    public static Optional<Product> of(String json) {
        try {
            return Optional.of(new Product(json));
        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    /**
     * Get the generic name of the product (eg. Rice Noodles)
     *
     * @return the generic name of the product
     */
    public String getGenericName() {
        try {
            return product.getString(GEN_NAME);
        } catch (JSONException e) {
            return UNKNOWN_NAME;
        }
    }

    /**
     * Get the full name of the product (eg. "Thai peanut noodle kit includes stir-fry
     * rice noodles & thai peanut seasoning")
     *
     * @return the full name of the product
     */
    public String getName() {
        try {
            return product.getString(FULL_NAME);
        } catch (JSONException e) {
            return UNKNOWN_NAME;
        }
    }

    /**
     * Get the amount of KCal within the product
     *
     * @return the amount of energy in the product in KCal
     */
    public int getKCalEnergy() {
        try {
            return product.getJSONObject(NUTRIMENTS).getInt(ENERGY_KCAL);
        } catch (JSONException e) {
            return UNKNOWN_VALUE;
        }
    }

}
