import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs32.examples.moshiExample.CSV.Searching.Searcher;
import edu.brown.cs32.examples.moshiExample.CSV.Searching.StringListCreator;
import edu.brown.cs32.examples.moshiExample.server.ActiveCSVWrapper;
import edu.brown.cs32.examples.moshiExample.server.FuzzTestHelper;
import edu.brown.cs32.examples.moshiExample.server.handlers.LoadHandler;
import edu.brown.cs32.examples.moshiExample.server.handlers.SearchHandler;
import edu.brown.cs32.examples.moshiExample.server.handlers.ViewHandler;
import edu.brown.cs32.examples.moshiExample.server.handlers.WeatherHandler;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static spark.Spark.after;

public class TestAPI {
    private static final Random randNum = new Random();

    @BeforeAll
    public static void setup_before_everything() {
        Spark.port(0);
//        Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
    }

    final ActiveCSVWrapper csv = new ActiveCSVWrapper();

    @BeforeEach
    public void setup() {
        this.csv.setActiveCSV(null);

        after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "*");
        });

        // Setting up the handler for the GET /order endpoint
        Spark.get("loadcsv", new LoadHandler(this.csv));
        Spark.get("viewcsv", new ViewHandler(this.csv));
        Spark.get("searchcsv", new SearchHandler(this.csv));
        Spark.get("weather", new WeatherHandler());
        Spark.init();
        Spark.awaitInitialization();
    }

    @AfterEach
    public void teardown() {
        // Gracefully stop Spark listening on both endpoints
        Spark.unmap("loadcsv");
        Spark.unmap("viewcsv");
        Spark.unmap("searchcsv");
        Spark.unmap("weather");
        Spark.awaitStop(); // don't proceed until the server is stopped
    }

    static private HttpURLConnection tryRequest(String apiCall) throws IOException {
        // Configure the connection (but don't actually send the request yet)
        URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
        HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

        clientConnection.connect();
        return clientConnection;
    }

    static private Map<String, Object> getResponse(HttpURLConnection clientConnection) throws IOException {
        //getting responses for the csv handlers
        BufferedReader in = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
        String line;
        StringBuilder builder = new StringBuilder();

        while((line = in.readLine()) != null) {
            builder.append(line);
        }

        in.close();
        String response = builder.toString();

        clientConnection.disconnect();

        Moshi moshi = new Moshi.Builder().build();
        Type mapOfParams = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapOfParams);
        return adapter.fromJson(response);
    }

    static private <T> T getResponse(HttpURLConnection clientConnection, Class<T> customClass) throws IOException {
        // getting responses for the weather handler
        Moshi moshi = new Moshi.Builder().build();
        return moshi.adapter(customClass).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    }

    // --------------------- LOAD HANDLER TESTS --------------------------------
    //too many parameters
    @Test
    public void testAPILoadTooManyParams() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=hi&headers=true&hello=world");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("error_bad_json: incorrect number of parameters provided", response.get("response_type"));
        assertEquals("hi", response.get("filepath"));
        assertEquals("true", response.get("headers"));
        assertEquals("http://localhost:" + Spark.port() + "/loadcsv", response.get("request" +
                ""));

        clientConnection.disconnect();
    }

    // too few parameters
    @Test
    public void testAPILoadTooFewParams() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=hi");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("error_bad_json: incorrect number of parameters provided", response.get("response_type"));
        assertEquals("hi", response.get("filepath"));
        assertEquals(null, response.get("headers"));
        assertEquals("http://localhost:" + Spark.port() + "/loadcsv", response.get("request" +
                ""));

        clientConnection.disconnect();
    }

    // filepath parameter missing
    @Test
    public void testAPILoadMissingFilePath() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?headers=false&hello=world");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("error_bad_request: filepath or headers parameter not provided", response.get("response_type"));
        assertEquals(null, response.get("filepath"));
        assertEquals("false", response.get("headers"));
        assertEquals("http://localhost:" + Spark.port() + "/loadcsv", response.get("request" +
                ""));

        clientConnection.disconnect();
    }

    // header parameter missing
    @Test
    public void testAPILoadMissingHeaders() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=nimtelson&hotel=trivago");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("error_bad_request: filepath or headers parameter not provided", response.get("response_type"));
        assertEquals("nimtelson", response.get("filepath"));
        assertEquals(null, response.get("headers"));
        assertEquals("http://localhost:" + Spark.port() + "/loadcsv", response.get("request" +
                ""));

        clientConnection.disconnect();
    }

    // header parameter is not "true" or "false"
    @Test
    public void testAPILoadHeadersWrongFormat() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=src/main/java/edu/brown/cs32/examples/moshiExample/data/stardata.csv&headers=t");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("error_bad_request: header parameter must be true or false", response.get("response_type"));
        assertEquals("src/main/java/edu/brown/cs32/examples/moshiExample/data/stardata.csv", response.get("filepath"));
        assertEquals("t", response.get("headers"));
        assertEquals("http://localhost:" + Spark.port() + "/loadcsv", response.get("request" +
                ""));

        clientConnection.disconnect();
    }

    //file not found
    @Test
    public void testAPILoadFileNotFound() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=hi&headers=true");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("error_datasource: file not found", response.get("response_type"));
        assertEquals("hi", response.get("filepath"));
        assertEquals("true", response.get("headers"));
        assertEquals("http://localhost:" + Spark.port() + "/loadcsv", response.get("request" +
                ""));

        clientConnection.disconnect();
    }

    //successful file load
    @Test
    public void testAPILoadSuccess() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=src/main/java/edu/brown/cs32/examples/moshiExample/data/ten-star.csv&headers=true");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("success", response.get("response_type"));
        assertEquals("src/main/java/edu/brown/cs32/examples/moshiExample/data/ten-star.csv", response.get("filepath"));
        assertEquals("true", response.get("headers"));
        assertEquals("http://localhost:" + Spark.port() + "/loadcsv", response.get("request" +
                ""));

        clientConnection.disconnect();
    }

    @Test
    public void testAPILoadSuccessEmpty() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=src/main/java/edu/brown/cs32/examples/moshiExample/data/empty.csv&headers=false");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("success", response.get("response_type"));
        assertEquals("src/main/java/edu/brown/cs32/examples/moshiExample/data/empty.csv", response.get("filepath"));
        assertEquals("false", response.get("headers"));
        assertEquals("http://localhost:" + Spark.port() + "/loadcsv", response.get("request" +
                ""));

        clientConnection.disconnect();
    }


    // --------------------- SEARCH HANDLER TESTS --------------------------------

    // csv not loaded
    @Test
    public void testAPISearchNoCSVLoaded() throws IOException {
        HttpURLConnection clientConnection = tryRequest("searchcsv?search=Rigel&column=1");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("error_datasource: no CSV loaded", response.get("response_type"));
        assertEquals("Rigel", response.get("search"));
        assertEquals("1", response.get("column"));
        assertEquals(new ArrayList<>(), response.get("data"));
        assertEquals("http://localhost:" + Spark.port() + "/searchcsv", response.get("request"));
        clientConnection.disconnect();
    }

    // too many parameters
    @Test
    public void testAPISearchTooManyParameters() throws IOException {
        this.csv.setActiveCSV(new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/ten-star.csv", true, new StringListCreator()));
        HttpURLConnection clientConnection = tryRequest("searchcsv?search=Rigel&column=1&hello=world");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("error_bad_json: too many parameters to search", response.get("response_type"));
        assertEquals("Rigel", response.get("search"));
        assertEquals("1", response.get("column"));
        assertEquals(new ArrayList<>(), response.get("data"));
        assertEquals("http://localhost:" + Spark.port() + "/searchcsv", response.get("request"));
        clientConnection.disconnect();
    }

    // too few parameters
    @Test
    public void testAPISearchTooFewParameters() throws IOException {
        this.csv.setActiveCSV(new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/ten-star.csv", true, new StringListCreator()));
        HttpURLConnection clientConnection = tryRequest("searchcsv");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("error_bad_request: no search parameter provided", response.get("response_type"));
        assertEquals(null, response.get("search"));
        assertEquals(null, response.get("column"));
        assertEquals(new ArrayList<>(), response.get("data"));
        assertEquals("http://localhost:" + Spark.port() + "/searchcsv", response.get("request"));
        clientConnection.disconnect();
    }

    // search term not provided
    @Test
    public void testAPISearchParamMissing() throws IOException {
        this.csv.setActiveCSV(new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/ten-star.csv", true, new StringListCreator()));
        HttpURLConnection clientConnection = tryRequest("searchcsv?column=1");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("error_bad_request: no search parameter provided", response.get("response_type"));
        assertEquals(null, response.get("search"));
        assertEquals("1", response.get("column"));
        assertEquals(new ArrayList<>(), response.get("data"));
        assertEquals("http://localhost:" + Spark.port() + "/searchcsv", response.get("request"));
        clientConnection.disconnect();
    }

    // column name is not a valid header
    @Test
    public void testAPISearchColumnMissing() throws IOException {
        this.csv.setActiveCSV(new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/ten-star.csv", true, new StringListCreator()));
        HttpURLConnection clientConnection = tryRequest("searchcsv?column=Nameasf&search=Rigel");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("error_bad_request: Column name Nameasf not found", response.get("response_type"));
        assertEquals("Rigel", response.get("search"));
        assertEquals("Nameasf", response.get("column"));
        assertEquals(new ArrayList<>(), response.get("data"));
        assertEquals("http://localhost:" + Spark.port() + "/searchcsv", response.get("request"));
        clientConnection.disconnect();
    }

    // column identifier search when CSV does not actually have headers
    @Test
    public void testAPISearchColumnNoHeaders() throws IOException {
        this.csv.setActiveCSV(new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/simpledata.csv", false, new StringListCreator()));
        HttpURLConnection clientConnection = tryRequest("searchcsv?column=animal&search=horse");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("error_datasource: This csv does not have headers", response.get("response_type"));
        assertEquals("horse", response.get("search"));
        assertEquals("animal", response.get("column"));
        assertEquals(new ArrayList<>(), response.get("data"));
        assertEquals("http://localhost:" + Spark.port() + "/searchcsv", response.get("request"));
        clientConnection.disconnect();
    }

    // provided index is out of bounds
    @Test
    public void testAPISearchOutOfBounds() throws IOException {
        this.csv.setActiveCSV(new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/simpledata.csv", false, new StringListCreator()));
        HttpURLConnection clientConnection = tryRequest("searchcsv?column=3&search=horse");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("error_bad_request: Index 3 out of bounds for length 3", response.get("response_type"));
        assertEquals("horse", response.get("search"));
        assertEquals("3", response.get("column"));
        assertEquals(new ArrayList<>(), response.get("data"));
        assertEquals("http://localhost:" + Spark.port() + "/searchcsv", response.get("request"));
        clientConnection.disconnect();
    }

    // normal successful search with no column identifier
    @Test
    public void testAPISearchNormal() throws IOException {
        this.csv.setActiveCSV(new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/simpledata.csv", false, new StringListCreator()));
        HttpURLConnection clientConnection = tryRequest("searchcsv?search=horse");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("success", response.get("response_type"));
        assertEquals("horse", response.get("search"));
        assertEquals(null, response.get("column"));
        assertEquals(List.of(List.of("horse","black","5")), response.get("data"));
        assertEquals("http://localhost:" + Spark.port() + "/searchcsv", response.get("request"));
        clientConnection.disconnect();
    }

    // successful search with index number
    @Test
    public void testAPISearchIndex() throws IOException {
        this.csv.setActiveCSV(new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/stardata.csv", true, new StringListCreator()));
        HttpURLConnection clientConnection = tryRequest("searchcsv?search=199.36567&column=2");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("success", response.get("response_type"));
        assertEquals("199.36567", response.get("search"));
        assertEquals("2", response.get("column"));
        assertEquals(List.of(List.of("12","Kaleigh","199.36567","0.14237","-144.63632")), response.get("data"));
        assertEquals("http://localhost:" + Spark.port() + "/searchcsv", response.get("request"));
        clientConnection.disconnect();
    }

    // successful search with column identifier
    @Test
    public void testAPISearchColumn() throws IOException {
        this.csv.setActiveCSV(new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/ten-star.csv", true, new StringListCreator()));
        HttpURLConnection clientConnection = tryRequest("searchcsv?search=Rigel&column=propername");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("success", response.get("response_type"));
        assertEquals("Rigel", response.get("search"));
        assertEquals("propername", response.get("column"));
        assertEquals(List.of(List.of("71454","Rigel Kentaurus B","-0.50359","-0.42128","-1.1767"), List.of(
                "71457","Rigel Kentaurus A","-0.50362","-0.42139","-1.17665")), response.get("data"));
        assertEquals("http://localhost:" + Spark.port() + "/searchcsv", response.get("request"));
        clientConnection.disconnect();
    }

    // normal successful search with empty csv
    @Test
    public void testAPISearchNormalEmpty() throws IOException {
        this.csv.setActiveCSV(new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/empty.csv", false, new StringListCreator()));
        HttpURLConnection clientConnection = tryRequest("searchcsv?search=horse");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("success", response.get("response_type"));
        assertEquals("horse", response.get("search"));
        assertEquals(null, response.get("column"));
        assertEquals(new ArrayList<>(), response.get("data"));
        assertEquals("http://localhost:" + Spark.port() + "/searchcsv", response.get("request"));
        clientConnection.disconnect();
    }
    // --------------------- VIEW HANDLER TESTS --------------------------------

    // file not loaded
    @Test
    public void testAPIViewNoCSVLoaded() throws IOException {
        HttpURLConnection clientConnection = tryRequest("viewcsv");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("error_datasource: no CSV loaded", response.get("response_type"));
        assertEquals(new ArrayList<>(), response.get("data"));
        assertEquals("http://localhost:" + Spark.port() + "/viewcsv", response.get("request"));
        clientConnection.disconnect();
    }

    // successful view
    @Test
    public void testAPIViewSuccess() throws IOException {
        this.csv.setActiveCSV(new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/simpledata.csv", false, new StringListCreator()));
        HttpURLConnection clientConnection = tryRequest("viewcsv");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);


        List<String> row1 = List.of("Frog","green","1");
        List<String> row2 = List.of("cow","brown","8");
        List<String> row3 = List.of("cat","orange","4");
        List<String> row4 = List.of("horse","black","5");
        assertNotNull(response);
        assertEquals("success", response.get("response_type"));
        assertEquals(List.of(row1,row2,row3,row4), response.get("data"));
        assertEquals("http://localhost:" + Spark.port() + "/viewcsv", response.get("request"));
        clientConnection.disconnect();
    }

    // empty file
    @Test
    public void testAPIViewSuccessEmpty() throws IOException {
        this.csv.setActiveCSV(new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/empty.csv", false, new StringListCreator()));
        HttpURLConnection clientConnection = tryRequest("viewcsv");
        assertEquals(200, clientConnection.getResponseCode());
        Map<String, Object> response = getResponse(clientConnection);
        assertNotNull(response);
        assertEquals("success", response.get("response_type"));
        assertEquals(new ArrayList<>(), response.get("data"));
        assertEquals("http://localhost:" + Spark.port() + "/viewcsv", response.get("request"));
        clientConnection.disconnect();
    }

    @Test
    public void testAPIWeatherProv() throws IOException {
        HttpURLConnection clientConnection = tryRequest("weather?lat=41.8268&longi=-71.4029");
        assertEquals(200, clientConnection.getResponseCode());
        WeatherHandler.WeatherSuccessResponse response = getResponse(clientConnection, WeatherHandler.WeatherSuccessResponse.class);
        System.out.println(response);
        assertNotNull(response);
        assertEquals("success", response.result());

        clientConnection.disconnect();
    }

    @Test
    public void testAPIWeatherLongCoords() throws IOException {
        HttpURLConnection clientConnection = tryRequest("weather?lat=41.82683284239819824838175&longi=-71.4029123721899462876419807");
        assertEquals(200, clientConnection.getResponseCode());
        WeatherHandler.WeatherSuccessResponse response = getResponse(clientConnection, WeatherHandler.WeatherSuccessResponse.class);

        assertNotNull(response);
        assertEquals("success", response.result());

        clientConnection.disconnect();
    }

    //out of bounds inputs
    @Test
    public void testAPIWeatherInvalidCoords() throws IOException {
        HttpURLConnection clientConnection = tryRequest("weather?lat=1000&longi=-1000");
        assertEquals(200, clientConnection.getResponseCode());
        WeatherHandler.WeatherFailureResponse response = getResponse(clientConnection, WeatherHandler.WeatherFailureResponse.class);

        assertNotNull(response);
        assertEquals("error_datasource", response.result());
        assertEquals("Server returned HTTP response code: 400 for URL: https://api.weather.gov/points/1000.0,-1000.0", response.error_message());

        clientConnection.disconnect();
    }

    //lattitude not inputted case
    @Test
    public void testAPIWeatherNoLat() throws IOException {
        HttpURLConnection clientConnection = tryRequest("weather?longi=-71.4029");
        assertEquals(200, clientConnection.getResponseCode());
        WeatherHandler.WeatherFailureResponse response = getResponse(clientConnection, WeatherHandler.WeatherFailureResponse.class);

        assertNotNull(response);
        assertEquals("error_bad_request", response.result());
        assertEquals("Input two parameters: Latitude and Longitude for success", response.error_message());

        clientConnection.disconnect();
    }

    //longitude not inputted case
    @Test
    public void testAPIWeatherNoLon() throws IOException {
        HttpURLConnection clientConnection = tryRequest("weather?lat=41.8268");
        assertEquals(200, clientConnection.getResponseCode());
        WeatherHandler.WeatherFailureResponse response = getResponse(clientConnection, WeatherHandler.WeatherFailureResponse.class);

        assertNotNull(response);
        assertEquals("error_bad_request", response.result());
        assertEquals("Input two parameters: Latitude and Longitude for success", response.error_message());

        clientConnection.disconnect();
    }

    //Latitude and longitude not given numbers instead words associated with it
    @Test
    public void testAPIWeatherNotNumbers() throws IOException {
        HttpURLConnection clientConnection = tryRequest("weather?lat=yuh&longi=yuh");
        assertEquals(200, clientConnection.getResponseCode());
        WeatherHandler.WeatherFailureResponse response = getResponse(clientConnection, WeatherHandler.WeatherFailureResponse.class);

        assertNotNull(response);
        assertEquals("error_bad_request", response.result());
        assertEquals("Input not a number, check input please", response.error_message());

        clientConnection.disconnect();
    }
    //Fuzz tests start from here


    /**
     * Generates Random Number
     */
    public static int randomNumGen(int min, int max) {
        int range = max-min;
        return randNum.nextInt(range) + min;
    }


    /**
     * Generates random string of certain length.
     * Comment out <int randomAscii = randomNum.nextInt(45, 127);> and write in
     * <int randomAscii = randomNum.nextInt(96) + 32;> in order to test with random spacing in between.
     *
     */
    public String randomStringGen(int length) {
        StringBuilder string = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            //int randomAscii = randomNum.nextInt(96) + 32;
            //no spaces
            int randomAscii = randNum.nextInt(45, 127);
            string.append((char) randomAscii);
        }
        return string.toString();
    }



    /**
     * Tests loading any random filepath.
     * should not fail because error for loading invalid filepath has been accounted for
     */
    @Test
    public void testFuzzLoadCSV() throws IOException {
        for (int i=0; i<1000; i++ ) {
            int rand = randomNumGen(0,100);
            String input = randomStringGen(rand);
            HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=" + input);
            int status = clientConnection.getResponseCode();
//
            if (status == 200) {
                assertEquals(200, clientConnection.getResponseCode());
                Map<String, Object> response = getResponse(clientConnection);
                assertNotNull(response);
                assertEquals(response.get("response_type"), response.get("response_type"));
                assertEquals("http://localhost:" + Spark.port() + "/loadcsv", response.get("request" +
                        ""));
            } else {
                fail("invalid status" + status);
            }

            clientConnection.disconnect();
        }

    }






    /**
     * Tests view with random input.
     * should not fail because error for loading multiple args w view has been accounted for
     */
    @Test
    public void testFuzzViewCSV() throws IOException {
        for (int i=0; i<1000; i++ ) {
            int rand = randomNumGen(0,100);
            String input = randomStringGen(rand);
            HttpURLConnection clientConnection = tryRequest("viewcsv?" + input);
            int status = clientConnection.getResponseCode();

            if (status != 200) {
                fail("invalid status" + status);
            }
            clientConnection.connect();
            clientConnection.disconnect();
        }

    }



    /**
     * searching without file
     * test will fail if status not 200
     */
    @Test
    public void fuzzSearchWithoutFile() throws IOException {
        for (int i=0; i<1000; i++ ) {
            int rand = randomNumGen(0,100);
            int rand2 = randomNumGen(0,100);
            String input = randomStringGen(rand);
            String input2 = randomStringGen(rand2);
            HttpURLConnection clientConnection = tryRequest("searchcsv?value=" + input+"&columID=" + input2);
            int status = clientConnection.getResponseCode();
            if (status != 200) {
                fail("invalid status" + status);
            }
            clientConnection.connect();
            clientConnection.disconnect();
        }
    }




    /**
     * fuzz test weather with correct lat and lon values
     * test will fail if status not 200
     */
    @Test
    public void fuzzWeatherWithCorrectInputs() throws IOException {
        for (int i = 0; i < 1000; i++) {
            int rand = randomNumGen(-90, 90);
            int rand2 = randomNumGen(-180, 180);
            String input = Integer.toString(rand);
            String input2 = Integer.toString(rand2);
            HttpURLConnection clientConnection = tryRequest("weather?lat=" + input + "&lon=" + input2);
            int status = clientConnection.getResponseCode();
            if (status != 200) {
                fail("invalid status" + status);
            }
            clientConnection.connect();
            clientConnection.disconnect();
        }
    }


    /**
     * fuzz test weather with abnormal lat and lon values
     * test will fail if status not 200
     */
    @Test
    public void fuzzWeatherWithAbnormalInputs() throws IOException {
        for (int i = 0; i < 1000; i++) {
            int rand = randomNumGen(1000000, 2000000);
            int rand2 = randomNumGen(1000000, 2000000);
            String input = Integer.toString(rand);
            String input2 = Integer.toString(rand2);
            HttpURLConnection clientConnection = tryRequest("weather?lat=" + input + "&lon=" + input2);
            int status = clientConnection.getResponseCode();
            if (status != 200) {
                fail("invalid status" + status);
            }
            clientConnection.connect();
            clientConnection.disconnect();
        }

}

    @Test
    public void testFuzz() throws IOException {
        String filepath = "";
        HttpURLConnection clientConnection = null;
        for(int i = 0; i < 1000; i++) {
            // pick a csv to load
            switch (FuzzTestHelper.getRandomIntBounded(0,2)) {
                case 0: filepath = "src/main/java/edu/brown/cs32/examples/moshiExample/data/simpledata.csv";
                    break;
                case 1: filepath = "src/main/java/edu/brown/cs32/examples/moshiExample/data/ten-star.csv";
                    break;
                case 2: filepath = "src/main/java/edu/brown/cs32/examples/moshiExample/data/stardata.csv";
            }


            //print out the response codes
            clientConnection = tryRequest("loadcsv?filepath=" + filepath + "&headers=false");
            System.out.println(clientConnection.getResponseCode());
            clientConnection = tryRequest("searchcsv?search=" + FuzzTestHelper.getRandomStringBounded(48, 126));
            System.out.println(clientConnection.getResponseCode());
        }
        clientConnection.disconnect();
    }
}