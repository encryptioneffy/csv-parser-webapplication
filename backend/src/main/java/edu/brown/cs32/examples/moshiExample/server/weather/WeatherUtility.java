package edu.brown.cs32.examples.moshiExample.server.weather;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs32.examples.moshiExample.server.Responder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * facilitates the weather API utilities
 */
public class WeatherUtility {
    public WeatherUtility() {
    }

    /**
     * NWS API call to retrieve forecast for a giveen coordinate
     * @param coords the desired coordinates
     * @return the response of the api call
     * @throws IOException if callAPI throws an IOException
     */
    public Responder.WeatherResponse forecastGetter(Coordinates coords) throws IOException {

        double lat = coords.lat();
        double longi = coords.longi();

        //clearly establishing our endpoint for the latitudes and longitudes, essentially
        //combining the returned coordinates with the website we are using
        String gridEndpoint = "https://api.weather.gov/points/" + lat + "," + longi;

        //getting the grids with the longitude and the latitude that has been returned
        Responder.GridResponse gridResponse = this.callAPI(gridEndpoint, Responder.GridResponse.class);
        String url = gridResponse.forecastURL().url();

        //finding our forecast by using the grids provided
        String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new java.util.Date());
        Responder.ForecastResponse forecastResponse = this.callAPI(url, Responder.ForecastResponse.class);
        Responder.Forecast finalForecast = forecastResponse.forecastPeriods().forecasts().get(0);

        return new Responder.WeatherResponse(finalForecast.temp(), finalForecast.unit(), timestamp);

    }

    /**
     * helper method for endpoint api construction and grid api
     * @param coords the desired coordinates
     * @return the correct endpoint from the coordinates provided
     */
    public String getGridEndpointURL(Coordinates coords) {

        double inputLat = coords.lat();
        double inputLon = coords.longi();

        // rounding to two decimal places
        double lat = Math.round(inputLat * 100) / 100.0;
        double lon = Math.round(inputLon * 100) / 100.0;

        return "https://api.weather.gov/points/" + lat + "," + lon;
    }

    /**
     * API call for a given url and converts response to specified class with moshi
     * @param url the url provided
     * @param classType the type of responder class to convert our response to
     * @param <T> the type of responder class
     * @return appropriate responder type for this api request
     * @throws IOException if we cannot open connection
     */

    public <T> T callAPI(String url, Type classType) throws IOException {
        URL endpoint = new URL(url);
        HttpURLConnection clientConnection = (HttpURLConnection) endpoint.openConnection();

        clientConnection.connect();
        int status = clientConnection.getResponseCode();

        switch (status) {
            case 404 -> throw new IOException("Invalid Coordinates, please recheck your input");
            case 500 -> throw new IOException("Error while connecting to NWS server, please try again");
        }

        InputStreamReader resultReader = new InputStreamReader(clientConnection.getInputStream());
        BufferedReader br = new BufferedReader(resultReader);
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line);
        }
        br.close();

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<T> adapter = moshi.adapter(classType);
        return adapter.fromJson(stringBuilder.toString());
    }

    /**
     * reader object converted to string
     * @param reader the reader object to read from
     * @return the string containing everything read from the reader
     * @throws IOException if we cannot open this reader
     */
    public String readerToString(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line);
        }
        br.close();

        return stringBuilder.toString();
    }

    /**
     * helper method to convert a string to a specific class
     * @param classType the class type to convert to
     * @param jsonString the json we want to convert
     * @param <T> the type to convert to
     * @return the json converted to the type provided
     * @throws IOException if the jsonString is not valid
     */
    public <T> T fromJson(Type classType, String jsonString) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<T> adapter = moshi.adapter(classType);
        return adapter.fromJson(jsonString);
    }

    /**
     * Returns the WeatherResponse instance for a given ForecastResponse to be wrapped, and adds a timestamp.
     * @param timestamp the timestamp provided to the request
     * @param forecastResponse the response from calling the weather api
     * @return the response of the request
     */
    public Responder.WeatherResponse getWeatherResponse(String timestamp, Responder.ForecastResponse forecastResponse) {
        Responder.Forecast forecast = forecastResponse.forecastPeriods().forecasts().get(0);
        return new Responder.WeatherResponse(forecast.temp(), forecast.unit(), timestamp);
    }


}

