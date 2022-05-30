package ch.epfl.sdp.healthplay.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class that represents the client used to get information
 * of products as a String given their bar codes.
 *
 * @author Ozan
 */
public final class ProductInfoClient {
    private final static String OPEN_FOOD_FACT_URL = "https://world.openfoodfacts.org/api/v0/product/";
    // The website asks us to provide a user agent of the following form:
    // User-Agent: NameOfYourApp - Android - Version 1.0 - www.yourappwebsite.com
    private final static String USER_AGENT = "HealthPlay - Android - Version 1.0 - https://github.com/HealthPlay-EPFL/Health-Play";
    // Workaround to the fact that we cannot use Optional values..
    private final static String ERROR_STRING = "ERROR";

    private final HttpURLConnection connection;

    // This is a class that just holds the method to get the product information,
    // there is no need to instantiate it.
    public ProductInfoClient(HttpURLConnection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null.");
        }
        this.connection = connection;
    }

    public ProductInfoClient(String barcode) throws IOException {
        if (barcode == null) {
            throw new IllegalArgumentException("Barcode cannot be null.");
        }
        this.connection = (HttpURLConnection)
                new URL(OPEN_FOOD_FACT_URL + barcode + ".json").openConnection();
    }

    /**
     * When called with a given barcode, connects to the openfoodfact API and
     * asks for the information of the given product. The result is returned
     * in a String that is in JSON format.
     *
     * @return the JSON string for that given product or "ERROR" if any error occurred
     */
    public String getInfo() {
        StringBuilder response = new StringBuilder();

        try {
            // Set a User-Agent in the HTTP header as asked by the openfoodfact website
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                try(BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                )) {
                    while ((line = bufferedReader.readLine()) != null)
                        response.append(line);

                    bufferedReader.close();

                    return response.toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return ERROR_STRING;
    }
}