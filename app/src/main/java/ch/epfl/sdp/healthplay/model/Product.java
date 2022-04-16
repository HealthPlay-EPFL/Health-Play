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
     * Get the amount of KCal per 100g within the product
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

    /**
     * Enum representing the nutriscore of a product
     */
    public enum Nutriscore {
        A, B, C, D, E, UNKNOWN
    }

    /**
     * Enum representing all the available nutriments present in a product
     */
    public enum Nutriments {
        ENERGY("energy"),
        ENERGY_KCAL("energy_kcal"),
        PROTEINS("proteins"),
        CASEIN("casein"),
        SERUM_PROTEIN("serum-proteins"),
        NUCLEOTIDES("nucleotides"),
        CARBOHYDRATES("carbohydrates"),
        SUGARS("sugars"),
        SUCROSE("sucrose"),
        GLUCOSE("glucose"),
        FRUCTOSE("fructose"),
        LACTOSE("lactose"),
        MALTOSE("maltose"),
        MALTODEXTRINS("maltodextrins"),
        STARCH("starch"),
        POLYOLS("polyols"),
        FAT("fat"),
        SATURATED_FAT("saturated-fat"),
        BUTYRIC_ACID("butyric-acid"),
        CAPORIC_ACID("caproic-acid"),
        CAPRYLIC_ACID("caprylic-acid"),
        CAPRIC_ACID("capric-acid"),
        LAURIC_ACID("lauric-acid"),
        MYRISTIC_ACID("myristic-acid"),
        PALMATIC_ACID("palmitic-acid"),
        STEARIC_ACID("stearic-acid"),
        ARACHIDIC_ACID("arachidic-acid"),
        BEHENIC_ACID("behenic-acid"),
        LIGNOCERIC_ACID("lignoceric-acid"),
        CEROTIC_ACID("cerotic-acid"),
        MONTANIC_ACID("montanic-acid"),
        MELISSIC_ACID("melissic-acid"),
        MONOUNSATURATED_FAT("monounsaturated-fat"),
        POLYUNSATURATED_FAT("polyunsaturated-fat"),
        OMEGA_3_FAT("omega-3-fat"),
        ALPHA_LINOLENIC_ACID("alpha-linolenic-acid"),
        EICOSAPENTAENOIC_ACID("eicosapentaenoic-acid"),
        DOCOSAHEXAENOIC_ACID("docosahexaenoic-acid"),
        OMEGA_6_FAT("omega-6-fat"),
        LINOLEIC_ACID("linoleic-acid"),
        ARACHIDONIC_ACID("arachidonic-acid"),
        GAMMA_LINOLENIC_ACID("gamma-linolenic-acid"),
        DIHOMO_GAMMA_LINOLENIC_ACID("dihomo-gamma-linolenic-acid"),
        OMEGA_9_FAT("omega-9-fat"),
        OLEIC_ACID("oleic-acid"),
        ELAIDIC_ACID("elaidic-acid"),
        GONODIC_ACID("gondoic-acid"),
        MEAD_ACID("mead-acid"),
        ERUCIC_ACID("erucic-acid"),
        NERVONIC_ACID("nervonic-acid"),
        TRANS_FAT("trans-fat"),
        CHOLESTEROL("cholesterol"),
        FIBER("fiber"),
        SODIUM("sodium"),
        SALT("salt"),
        ALCOHOL("alcohol"), // % vol of alcohol
        VITAMIN_A("vitamin-a"),
        VITAMIN_D("vitamin-d"),
        VITAMIN_E("vitamin-e"),
        VITAMIN_K("vitamin-k"),
        VITAMIN_C("vitamin-c"),
        VITAMIN_B1("vitamin-b1"),
        VITAMIN_B2("vitamin-b2"),
        VITAMIN_PP("vitamin-pp"),
        VITAMIN_B6("vitamin-b6"),
        VITAMIN_B9("vitamin-b9"),
        VITAMIN_B12("vitamin-b12"),
        BIOTIN("biotin"),
        PANTOTHENIC_ACID("pantothenic-acid"),
        SILICIA("silica"),
        BICARBONATE("bicarbonate"),
        POTASSIUM("potassium"),
        CHLORID("chloride"),
        CALCUIM("calcium"),
        PHOSPHORUS("phosphorus"),
        IRON("iron"),
        MAGNESIUM("magnesium"),
        ZINC("zinc"),
        COPPER("copper"),
        MANGANESE("manganese"),
        FLUORIDE("fluoride"),
        SELENIUM("selenium"),
        CHROMIUM("chromium"),
        MOLYBDENUM("molybdenum"),
        IODINE("iodine"),
        CAFFEINE("caffeine"),
        TAURINE("taurine");

        private final static String SERVING = "_serving";
        private final static String G_100 = "_100g";
        private final static String UNIT = "_unit";
        private final static String VALUE = "_value";

        private final String name;

        Nutriments(String name) {
            this.name = name;
        }

        /**
         * Get the String representation of that nutriment
         *
         * @return the String representation of that nutriment
         */
        public String getName() {
            return name;
        }
    }
}
/*
{
   "calcium":0.038,
   "calcium_100g":0.038,
   "calcium_serving":0.0198,
   "calcium_unit":"mg",
   "calcium_value":38,
   "carbohydrates":71.15,
   "carbohydrates_100g":71.15,
   "carbohydrates_serving":37,
   "carbohydrates_unit":"g",
   "carbohydrates_value":71.15,
   "cholesterol":0,
   "cholesterol_100g":0,
   "cholesterol_serving":0,
   "cholesterol_unit":"mg",
   "cholesterol_value":0,
   "energy":1611,
   "energy-kcal":385,
   "energy-kcal_100g":385,
   "energy-kcal_serving":200,
   "energy-kcal_unit":"kcal",
   "energy-kcal_value":385,
   "energy_100g":1611,
   "energy_serving":838,
   "energy_unit":"kcal",
   "energy_value":385,
   "fat":7.69,
   "fat_100g":7.69,
   "fat_serving":4,
   "fat_unit":"g",
   "fat_value":7.69,
   "fiber":1.9,
   "fiber_100g":1.9,
   "fiber_serving":0.988,
   "fiber_unit":"g",
   "fiber_value":1.9,
   "fruits-vegetables-nuts-estimate-from-ingredients_100g":0,
   "fruits-vegetables-nuts-estimate-from-ingredients_serving":0,
   "iron":0.00069,
   "iron_100g":0.00069,
   "iron_serving":0.000359,
   "iron_unit":"mg",
   "iron_value":0.69,
   "nova-group":4,
   "nova-group_100g":4,
   "nova-group_serving":4,
   "nutrition-score-fr":4,
   "nutrition-score-fr_100g":4,
   "proteins":9.62,
   "proteins_100g":9.62,
   "proteins_serving":5,
   "proteins_unit":"g",
   "proteins_value":9.62,
   "salt":0.72,
   "salt_100g":0.72,
   "salt_serving":0.374,
   "salt_unit":"mg",
   "salt_value":720,
   "saturated-fat":1.92,
   "saturated-fat_100g":1.92,
   "saturated-fat_serving":0.998,
   "saturated-fat_unit":"g",
   "saturated-fat_value":1.92,
   "sodium":0.288,
   "sodium_100g":0.288,
   "sodium_serving":0.15,
   "sodium_unit":"mg",
   "sodium_value":288,
   "sugars":13.46,
   "sugars_100g":13.46,
   "sugars_serving":7,
   "sugars_unit":"g",
   "sugars_value":13.46,
   "trans-fat":0,
   "trans-fat_100g":0,
   "trans-fat_serving":0,
   "trans-fat_unit":"g",
   "trans-fat_value":0,
   "vitamin-a":0.0001155,
   "vitamin-a_100g":0.0001155,
   "vitamin-a_serving":6.01e-05,
   "vitamin-a_unit":"IU",
   "vitamin-a_value":385,
   "vitamin-c":0,
   "vitamin-c_100g":0,
   "vitamin-c_serving":0,
   "vitamin-c_unit":"mg",
   "vitamin-c_value":0
}
*/