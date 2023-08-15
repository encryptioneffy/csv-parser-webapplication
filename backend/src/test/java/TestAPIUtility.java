import Mocks.MockJSON;
import Mocks.WeatherUtilityMocker;
import edu.brown.cs32.examples.moshiExample.server.Responder;
import edu.brown.cs32.examples.moshiExample.server.handlers.WeatherHandler;
import edu.brown.cs32.examples.moshiExample.server.weather.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestAPIUtility {


    @Test
    public void testStringFromReader() throws IOException {
        //mock reader object created which simulates the connections input reader
        WeatherUtility weatherUtility = new WeatherUtility();
        StringReader stringReader = new StringReader(MockJSON.mockProvidenceForecast);
        String jsonNoFormat = MockJSON.mockProvidenceForecast.replace("\n", "");
        assertEquals(jsonNoFormat, weatherUtility.readerToString(stringReader));
    }

    @Test
    public void testEndpointURLFromCoords() {
        WeatherUtility weatherUtil = new WeatherUtility();
        Coordinates coords = new Coordinates(10.4, -77.3);
        assertEquals("https://api.weather.gov/points/10.4,-77.3", weatherUtil.getGridEndpointURL(coords));
    }


    @Test
    public void testRoundEndpointURLFromCoords() {
        WeatherUtility weatherUtil = new WeatherUtility();
        Coordinates coords = new Coordinates(10.4192938174662, -77.3103975527194);
        assertEquals("https://api.weather.gov/points/10.42,-77.31", weatherUtil.getGridEndpointURL(coords));
    }

    //test if forecast url returned by API is properly parsed
    @Test
    public void testFromJsonForecastURL() throws IOException {
        WeatherUtility weatherUtil = new WeatherUtility();
        Responder.GridResponse gridResponse = weatherUtil.fromJson(Responder.GridResponse.class, MockJSON.mockProvidenceURLs);
        assertEquals("https://api.weather.gov/gridpoints/BOX/64,63/forecast", gridResponse.forecastURL().url());
    }

    //tests if data returned to call by API is properly parsed
    @Test
    public void testFromJsonForecast() throws IOException {
        WeatherUtility weatherUtil = new WeatherUtility();
        Responder.ForecastResponse forecastResponse = weatherUtil.fromJson(Responder.ForecastResponse.class, MockJSON.mockProvidenceForecast);
        assertEquals(14, forecastResponse.forecastPeriods().forecasts().size());
        assertEquals(51, forecastResponse.forecastPeriods().forecasts().get(0).temp());
        assertEquals("F", forecastResponse.forecastPeriods().forecasts().get(0).unit());
        assertEquals(31, forecastResponse.forecastPeriods().forecasts().get(1).temp());
        assertEquals("F", forecastResponse.forecastPeriods().forecasts().get(1).unit());

    }

    //weatherresponse instance returned by getweatherresponse resturns the correct values
    @Test
    public void testGetWeatherResponse() throws IOException {
        WeatherUtility weatherUtils = new WeatherUtility();
        Responder.ForecastResponse forecastResponse = weatherUtils.fromJson(Responder.ForecastResponse.class, MockJSON.mockProvidenceForecast);
        String mockTime = "2023-03-02T19:53:16+00:00";
        Responder.WeatherResponse response = weatherUtils.getWeatherResponse(mockTime, forecastResponse);
        assertEquals(51, response.temp());
        assertEquals("F", response.unit());
        assertEquals(mockTime, response.timestamp());
    }


    //successs serialization is functioning in an appropriate manner
    @Test
    public void testWeatherSuccessSerialize() throws IOException {
        String mockTime = "2023-03-02T19:53:16+00:00";
        WeatherUtilityMocker mockWeatherUtils = new WeatherUtilityMocker();
        Responder.ForecastResponse forecastResponse = mockWeatherUtils.fromJson(Responder.ForecastResponse.class, MockJSON.mockProvidenceForecast);
        Responder.WeatherResponse response = mockWeatherUtils.getWeatherResponse(mockTime, forecastResponse);

        WeatherHandler weatherHandler = new WeatherHandler();
        assertEquals(MockJSON.mockServerExpectedNewYorkCity, weatherHandler.getSerializedSuccess(40.73, -73.94, response));
    }

    //failure serilizes the results correctly
    @Test
    public void testWeatherFailureSerialize() throws IOException {
        WeatherHandler weatherHandler = new WeatherHandler();
        String serializedFailure = weatherHandler.getSerializedFailure("error_bad_json", "Some error message!");
        assertEquals(MockJSON.mockExpectedWeatherFailure, serializedFailure);
    }

    //identical caches for the proxy checking if chaching is functioning
    @Test
    public void testWeatherProxyCacheIdentical() {
        WeatherUtilityMocker mockWeatherUtils = new WeatherUtilityMocker();
        Proxy proxy = new Proxy(mockWeatherUtils, 0.3, 10, 2, TimeUnit.HOURS);
        Coordinates nyCoords = new Coordinates(40.73, -73.94);
        proxy.getForecast(nyCoords);
        assertTrue(proxy.isCachedExact(nyCoords));

        Responder.WeatherResponse res = proxy.getForecast(nyCoords);
        assertEquals(55, res.temp());
        assertEquals("F", res.unit());
    }

    //checking if cahching is working even if coordinates are very close to one another
    @Test
    public void testWeatherProxyCacheClose() {
        WeatherUtilityMocker mockWeatherUtils = new WeatherUtilityMocker();
        Proxy proxy = new Proxy(mockWeatherUtils, 0.3, 10, 2, TimeUnit.HOURS);
        Coordinates nyCoords = new Coordinates(40.73, -73.94);
        proxy.getForecast(nyCoords);

        Coordinates closeNyCoords = new Coordinates(40.60, -73.80);
        assertFalse(proxy.isCachedExact(closeNyCoords));
        assertEquals(nyCoords, proxy.getClosest(closeNyCoords));

        Responder.WeatherResponse res = proxy.getForecast(closeNyCoords);
        assertEquals(55, res.temp());
        assertEquals("F", res.unit());
    }

    //new data is given at time of expiry
    @Test
    public void testWeatherProxyExpiry() throws InterruptedException {
        WeatherUtilityMocker mockWeatherUtils = new WeatherUtilityMocker();
        Proxy proxy = new Proxy(mockWeatherUtils, 0.3, 10, 1, TimeUnit.SECONDS);
        Coordinates nyCoords = new Coordinates(40.73, -73.94);
        proxy.getForecast(nyCoords);
        assertTrue(proxy.isCachedExact(nyCoords));
        TimeUnit.SECONDS.sleep(1);
        assertFalse(proxy.isCachedExact(nyCoords));
    }

    //same object returned no matter what precision
    @Test
    public void testGetClosestNotFound() {
        WeatherUtilityMocker mockWeatherUtils = new WeatherUtilityMocker();
        Proxy proxy = new Proxy(mockWeatherUtils, 0.3, 10, 2, TimeUnit.HOURS);
        Coordinates nyCoordz = new Coordinates(40.73, -73.94);
        assertEquals(nyCoordz, proxy.getClosest(nyCoordz));
    }

}
