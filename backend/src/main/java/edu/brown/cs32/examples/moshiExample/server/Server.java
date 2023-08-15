package edu.brown.cs32.examples.moshiExample.server;

import static spark.Spark.after;


import edu.brown.cs32.examples.moshiExample.CSV.Parsing.Parser;
import edu.brown.cs32.examples.moshiExample.CSV.Searching.Star;
import edu.brown.cs32.examples.moshiExample.CSV.Searching.StarCreator;
import edu.brown.cs32.examples.moshiExample.server.handlers.LoadHandler;
import edu.brown.cs32.examples.moshiExample.server.handlers.SearchHandler;
import edu.brown.cs32.examples.moshiExample.server.handlers.ViewHandler;
import edu.brown.cs32.examples.moshiExample.server.handlers.WeatherHandler;
import spark.Spark;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

/**
 * Top-level class for this demo. Contains the main() method which starts Spark and runs the various handlers.
 *
 * We have two endpoints in this demo. They need to share state (a menu).
 * This is a great chance to use dependency injection, as we do here with the menu set. If we needed more endpoints,
 * more functionality classes, etc. we could make sure they all had the same shared state.
 */
public class Server {
    public static void main(String[] args) {
       Spark.port(3005);
       /*
           Setting CORS headers to allow cross-origin requests from the client; this is necessary for the client to
           be able to make requests to the server.

           By setting the Access-Control-Allow-Origin header to "*", we allow requests from any origin.
           This is not a good idea in real-world applications, since it opens up your server to cross-origin requests
           from any website. Instead, you should set this header to the origin of your client, or a list of origins
           that you trust.

           By setting the Access-Control-Allow-Methods header to "*", we allow requests with any HTTP method.
           Again, it's generally better to be more specific here and only allow the methods you need, but for
           this demo we'll allow all methods.

           We recommend you learn more about CORS with these resources:
               - https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
               - https://portswigger.net/web-security/cors
        */
       after((request, response) -> {
           response.header("Access-Control-Allow-Origin", "*");
           response.header("Access-Control-Allow-Methods", "*");
       });

       // Setting up the handler for the GET /loadcsv endpoint
       // we need to create a container for the csv searcher, setting the searcher to null doesn't work.
       // right now if the filename isn't found, the whole program errors. Need to update the Searcher class to not
       // do that
       ActiveCSVWrapper currentCSV = new ActiveCSVWrapper();
       Spark.get("loadcsv", new LoadHandler(currentCSV));
       Spark.get("viewcsv", new ViewHandler(currentCSV));
       Spark.get("searchcsv", new SearchHandler(currentCSV));
       Spark.get("weather", new WeatherHandler());
       Spark.init();
       Spark.awaitInitialization();
       System.out.println("Server started.");
        // FuzzTestHelper fuzz = new FuzzTestHelper();
        // for (int i = 0; i < 100; i++) {
        //     System.out.println(fuzz.getRandomStringBounded(48, 125));
        // }
    }
}
