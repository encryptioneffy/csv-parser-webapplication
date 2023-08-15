package edu.brown.cs32.examples.moshiExample.server;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Different reponses are given here, a neat way of organizing different reponses and ensuring the correct data
 * is stored in the correct manner
 */
public class Responder {

    public record GridResponse(
            @Json(name = "properties") ForecastURL forecastURL
    ){}

    public record ForecastURL(
            @Json(name = "forecast") String url
    ){}

    public record ForecastResponse(
            @Json(name = "properties") ForecastPeriods forecastPeriods
    ){}

    public record ForecastPeriods(
            @Json(name = "periods") List<Forecast> forecasts
    ){}

    public record Forecast(
            @Json(name = "temperature") int temp, @Json(name = "temperatureUnit") String unit
    ) {}

    public record WeatherResponse(int temp, String unit, String timestamp) {

    }

    /**
     * Response object to send, containing the parameters to the request and the result of the request
     * @param response_type String that says whether the request was successful or detailed error message
     * @param url the url of the request made
     * @param filepath the filepath provided to the request, or null if no filepath provided
     * @param headers the value of the headers parameter provided, or null if no header param provided
     */
    public record LoadCSVResponse(String response_type, String url, String filepath, String headers) {
        /**
         * Method to serialize the result of the request and display appropriate json string
         * @return String containing the json of the result of the request
         */
        public String serialize() {
            try {
                // create new moshi builder
                Moshi moshi = new Moshi.Builder().build();
                //add our fields to the map!
                HashMap<String, Object> m = new HashMap<>();
                m.put("response_type", response_type);
                m.put("request", url);
                m.put("filepath", filepath);
                m.put("headers", headers);
                //serialize the map
                Type mapOfParams = Types.newParameterizedType(Map.class, String.class, Object.class);
                JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapOfParams);
                return adapter.toJson(m);
            } catch(Exception e) {
                // For debugging purposes, show in the console _why_ this fails
                // Otherwise we'll just get an error 500 from the API in integration
                // testing.
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * Response object to send, containing the params to the request and the result of the request
     * @param response_type String that says whether the request was successful or detailed error message
     * @param url the url of the request made
     * @param search the search term provided, or null if no search parameter was given
     * @param col the column identifier or index provided, or null if no column parameter was given
     * @param csv the result of applying search with the parameters given or empty if the search was unsuccessful
     */
    public record SearchCSVResponse(String response_type, String url, String search, String col, List<List<String>> csv) {
        /**
         * Method to serialize the result of the request and display appropriate json string
         * @return String containing the json of the result of the request
         */

        public String serialize() {
            try {
                // create new moshi builder
                Moshi moshi = new Moshi.Builder().build();
                //add our fields to the map!
                HashMap<String, Object> m = new HashMap<>();
                m.put("response_type", response_type);
                m.put("request", url);
                m.put("search", search);
                m.put("column", col);
                m.put("data", csv);
                //serialize the map
                Type mapOfParams = Types.newParameterizedType(Map.class, String.class, Object.class);
                JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapOfParams);
                return adapter.toJson(m);
            } catch(Exception e) {
                // For debugging purposes, show in the console _why_ this fails
                // Otherwise we'll just get an error 500 from the API in integration
                // testing.
                e.printStackTrace();
                throw e;
            }
        }
    }
    /**
     * Response object to send, containing the params to the request and the result of the request
     * @param response_type String that says whether the request was successful or detailed error message
     * @param url the url of the request made
     * @param csv the data of the csv being viewed, or empty if the request was unsuccessful
     */
    public record ViewCSVResponse(String response_type, String url, List<List<String>> csv) {
        /**
         * Method to serialize the result of the request and display appropriate json
         * @return String containing the json of the result of the request
         */
        public String serialize() {
            try {
                // create new moshi builder
                Moshi moshi = new Moshi.Builder().build();
                // add our fields to our map!
                HashMap<String, Object> m = new HashMap<>();
                m.put("response_type", response_type);
                m.put("data", csv);
                m.put("request", url);
                //serialize the map
                Type mapOfParams = Types.newParameterizedType(Map.class, String.class, Object.class);
                JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapOfParams);
                return adapter.toJson(m);
            } catch(Exception e) {
                // For debugging purposes, show in the console _why_ this fails
                // Otherwise we'll just get an error 500 from the API in integration
                // testing.
                e.printStackTrace();
                throw e;
            }
        }
    }
}

