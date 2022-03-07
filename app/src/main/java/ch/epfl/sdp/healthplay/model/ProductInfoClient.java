package ch.epfl.sdp.healthplay.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class that represents the client used to get information
 * of products as a String given their bar codes.
 *
 * @author Ozan
 */
public final class ProductInfoClient {
    //TODO: User-Agent: NameOfYourApp - Android - Version 1.0 - www.yourappwebsite.com
    private final static String OPEN_FOOD_FACT_URL = "https://world.openfoodfacts.org/api/v0/product/";
    private final static String USER_AGENT = "HealthPlay - Android - Version 1.0 - https://github.com/HealthPlay-EPFL/Health-Play";
    // Workaround to the fact that we cannot use Optional values..
    private final static String ERROR_STRING = "ERROR";

    // This is a class that just holds the method to get the product information,
    // there is no need to instantiate it.
    private ProductInfoClient() {}

    /**
     * When called with a given barcode, connects to the openfoodfact API and
     * asks for the information of the given product. The result is returned
     * in a String that is in JSON format.
     *
     * @param barcode the barcode to get information from
     * @return the JSON string for that given product or "ERROR" if any error occurred
     */
    public static String getInfo(String barcode) {
        URL url;
        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();

        try {
            url = new URL(OPEN_FOOD_FACT_URL + barcode + ".json");
            connection = (HttpURLConnection) url.openConnection();
            // Set a User-Agent in the HTTP header as asked by the openfoodfact website
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );

                while ((line = bufferedReader.readLine()) != null)
                    response.append(line);

                bufferedReader.close();

                return response.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return ERROR_STRING;
    }
}
