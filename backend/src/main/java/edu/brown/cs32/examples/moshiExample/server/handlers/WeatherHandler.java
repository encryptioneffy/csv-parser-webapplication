package edu.brown.cs32.examples.moshiExample.server.handlers;

import com.google.common.util.concurrent.UncheckedExecutionException;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs32.examples.moshiExample.server.weather.Coordinates;
import edu.brown.cs32.examples.moshiExample.server.weather.Proxy;
import edu.brown.cs32.examples.moshiExample.server.Responder;
import edu.brown.cs32.examples.moshiExample.server.weather.WeatherUtility;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.concurrent.TimeUnit;

/**
 * Class used to fetch weather related data
 */
public class WeatherHandler implements Route {
    WeatherUtility utility;
    Proxy proxy;

    public WeatherHandler() {
        this.utility = new WeatherUtility();
        this.proxy = new Proxy(this.utility, 0.3, 10, 2, TimeUnit.HOURS);
    }

    /**
     * lat and longi used as queries in NWS weather API for weather data
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        String paramLat = request.queryParams("lat");
        String paramLongi = request.queryParams("longi");
        double lat;
        double longi;

        //checking if both inputs are put in
        if (paramLat == null || (paramLongi == null)) {
                return new WeatherFailureResponse("error_bad_request", "Input two parameters: Latitude and Longitude for success").serialize();
        }


        try {
        lat = Double.parseDouble(paramLat);
        longi = Double.parseDouble(paramLongi);
        } catch (NumberFormatException e) {
            return new WeatherFailureResponse("error_bad_request", "Input not a number, check input please").serialize();
        }

        try {
        Coordinates coords = new Coordinates(lat, longi);
        Responder.WeatherResponse forecast = this.proxy.getForecast(coords);
        int temp = forecast.temp();
        String unit = forecast.unit();
        String timestamp = forecast.timestamp();

        return new WeatherSuccessResponse(lat, longi, temp, unit, timestamp).serialize();

        } catch (UncheckedExecutionException e) {
            return new WeatherFailureResponse("error_datasource", e.getCause().getMessage()).serialize();
        }
    }

    /**
     * Success response helper method
     */
    public record WeatherSuccessResponse(String result, double latitude, double longitude, int temp, String unit, String timestamp) {
        public WeatherSuccessResponse(double lat, double lon, int temp, String unit, String timestamp) {
            this("success", lat, lon, temp, unit, timestamp);
        }

        String serialize() {
            try {
                Moshi moshi = new Moshi.Builder()
                        .build();
                JsonAdapter<WeatherSuccessResponse> adapter = moshi.adapter(WeatherSuccessResponse.class);
                return adapter.toJson(this);
            } catch(Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }


    /**
     * Failure helper method similar to success
     */
    public record WeatherFailureResponse(String result, String error_message) {
        public WeatherFailureResponse(String error_message) { this("error", error_message); }

        String serialize() {
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(WeatherFailureResponse.class).toJson(this);
        }
    }

    /**
     * helper method for the successes
     * @param lat
     * @param lon
     * @param response
     * @return
     */
    public String getSerializedSuccess(double lat, double lon, Responder.WeatherResponse response) {
        int temp = response.temp();
        String unit = response.unit();
        String timestamp = response.timestamp();
        return new WeatherSuccessResponse(lat, lon, temp, unit, timestamp).serialize();
    }

    /**
     * Helper method for the failure
     * @param errorType
     * @param errorMessage
     * @return
     */
    public String getSerializedFailure(String errorType, String errorMessage) {
        return new WeatherFailureResponse(errorType, errorMessage).serialize();
    }
}
