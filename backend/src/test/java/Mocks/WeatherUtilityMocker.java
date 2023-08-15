package Mocks;

import edu.brown.cs32.examples.moshiExample.server.weather.Coordinates;
import edu.brown.cs32.examples.moshiExample.server.Responder;
import edu.brown.cs32.examples.moshiExample.server.weather.WeatherUtility;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Mocker for testing purposes
 */
public class WeatherUtilityMocker extends WeatherUtility {

    @Override
    public Responder.WeatherResponse forecastGetter(Coordinates coords) throws IOException {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new java.util.Date());
        Responder.ForecastResponse forecastResponse = this.fromJson(Responder.ForecastResponse.class, MockJSON.mockNewYorkCityForecast);
        return getWeatherResponse(timestamp, forecastResponse);
    }
}
