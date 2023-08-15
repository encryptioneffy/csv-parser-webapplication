Cs logins: bpajotte, akadaki2
Link to github repo: https://github.com/cs0320-s2023/sprint-3-akadaki2-bpajotte

Design Choices:

We have developed a class called ActiveCSVWrapper for the shared CSV state of our API. We instantiated a common instance 
of this class and passed it to the CSV related handlers. This class keeps track of the currently loaded CSV file and its
headers (if available).

To handle weather requests, we created multiple specialized classes, most of which are located in the weather/ folder. 
WeatherUtility contains methods for retrieving and parsing data from the NWS API which are used in WeatherHandler. 
WeatherProxy wraps or WeatherUtility class and adds caching functionality using the Google Guava cache library. 
WeatherHandler inherits from a standard Moshi handler and connects all the functionality together. It retrieves data 
from the API using WeatherUtility and Proxy, parses it, and returns it in JSON form using Moshi's serialize method.

Proxy includes default parameters for the caching system, such as the cache's maximum size and data expiry time. 
Coordinates is a record representing a geographical coordinate, and instances of this record are used as 
keys to retrieve data stored in the cache. We chose to use a record because it allowed us to store both coordinates in 
one structure, and the .equals and .hashCode method were automatically implemented.

WeatherResponse is a record used to hold the final responses from our Server's Weather API. It contains temperature, 
temperature unit, and timestamp for the request. Responder contains multiple classes used to parse the different JSON 
data returned by the NWS API into queryable records. It is located in the folder.

For backend testing, we have created two classes specifically for mocking: MockJSON and MockWeatherUtility. 
MockJSON contains multiple mock responses from the NWS API/expected responses from our server, which are extensively 
used in backend tests (i.e., in the TestAPIUtilities file). MockWeatherUtility extends the WeatherUtility class and 
overrides the usual getForecast method to provide mock data, mostly used to test caching.

Testing:

Integration Tests:
weather
- getting weather for Providence coords
- getting weather with double coordinates with many digits 
- getting weather with invalid coords 
- getting weather without param 
- getting weather without param 
- getting weather with coords that are not numbers

loadcsv
- loading a csv with too many parameters
- loading a csv with too few parameters
- loading a csv with filepath parameter missing
- loading a csv with header parameter missing
- loading a csv with invalid header parameter 
- loading a csv with invalid filepath
- successfully loading csv with data
- successfully loading and empty csv

searchcsv
- searching a csv that hasn't been loaded yet
- searching a csv with too many parameters
- searching a csv with too few parameters
- searching a csv with search parameter missing
- searching a csv with invalid column parameter
- searching a csv that does not have headers with column parameter provided
- searching a csv with a column index that is out of bounds
- successfully searching a csv with no column index or identifier
- successfully searching a csv with a column index
- successfully searching an empty csv

viewcsv
- viewing a csv that hasn't been loaded yet
- successfully viewing a csv with data
- successfully viewing an empty csv

Unit Tests:
Weather: 
- Properly converting Reader objects (returned by the API call) to Strings. 
- Creating a correct NWS URL to call, based on given coordinates. 
- Rounding coordinates to the second decimal place to generate a NWS URL. 
- Extracting the forecast data URL from the original NWS API call. 
- Parsing the forecast data into a ForecastResponse record that can be queried. 
- Generating a WeatherResponse object with valid temperature, unit, and timestamp, based on a ForecastResponse. 
- Converting a WeatherResponse instance into a JSON response using WeatherSuccess serialization. 
- Generating an error JSON response using WeatherFailure serialization. 
- Automatic caching by WeatherProxy when retrieving a forecast. 
- Retrieving a previously cached forecast when the requested coordinates are close to the cached ones. 
- Expiring the cache after a specified delay, passed as a parameter to WeatherProxy. 
- Retrieving a forecast for uncached coordinates, and ensuring that WeatherProxy passes the exact coordinates to 
- WeatherUtility's getForecast method.
Searcher:
- Search term not found in file
- Successful search without column identifier
- Search term not found in the provided column index
- Search term found in provided column index
- Search term found in provided column identifier
- Successful search when search term contains spaces
Parser:
- Successfully parses empty csv
- Successfully parses csv with one row
- Successfully parses csv with headers
- Successfully parses csv with a different creator from row structure

Errors/Bugs:
We are running into an error during testing where our caching doesn't seem to be working as intended. See
testWeatherProxyExpiry in TestAPIUtility

How to Run:
To run the tests, run “mvn test” in the terminal.

To start the server, navigate to the server package and run the main method in the Server class. This will start the
port. In your browser, navigate to localhost:3000/<your desired endpoint> to start! If this is unsuccessful, try editing
the port number in the Server file in our server package and run the above command again with the new port number.

- The first option is “loadcsv?filepath=<filepath>” if you want to load a csv file.
- The second option is “viewcsv” if you want to view a loaded file.
- The third option is “searchcsv?search=<search_value>” if you want to search for a value within the file. You can also 
search within a specific column by “searchcsv?search=<search_value>&column=<column name or index>”. 
- The last option is”weather?lat=<latitude>&lon=<longitude>” if you want to get the weather forecast at the specified 
location.