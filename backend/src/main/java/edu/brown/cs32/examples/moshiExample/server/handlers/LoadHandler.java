package edu.brown.cs32.examples.moshiExample.server.handlers;

import edu.brown.cs32.examples.moshiExample.CSV.Searching.Searcher;
import edu.brown.cs32.examples.moshiExample.CSV.Searching.StringListCreator;
import edu.brown.cs32.examples.moshiExample.server.ActiveCSVWrapper;
import edu.brown.cs32.examples.moshiExample.server.Responder.LoadCSVResponse;
import spark.Request;
import spark.Response;
import spark.Route;


import java.util.Set;

/**
 * Handler class for the loadcsv API endpoint.
 */
public class LoadHandler implements Route {
    private ActiveCSVWrapper activeCSV;

    /**
     * Constructor accepts some shared active csv
     * @param csv the shared csv
     */
    public LoadHandler(ActiveCSVWrapper csv) {
        this.activeCSV = csv;
    }

    /**
     * Handle the API request
     * @param request the request to handle
     * @param response use to modify properties of the response
     * @return correct response for request
     * @throws Exception This is part of the interface; we don't have to throw anything.
     */

    @Override
    public Object handle(Request request, Response response) throws Exception {
        // get our parameters!
        Set<String> parameters = request.queryParams();
        String url = request.url();
        String filepath = request.queryParams("filepath");
        String headers = request.queryParams("headers");



        if (parameters.size() != 2) {
            // error because it doesn't have too many arguments or too few arguments. expects filepath and headers bool
            return new LoadCSVResponse("error_bad_json: incorrect number of parameters provided", url, filepath, headers).serialize();
        }

        //making sure there are actually the correct params provided
        if(filepath == null || headers == null) {
            return new LoadCSVResponse("error_bad_request: filepath or headers parameter not provided", url, filepath, headers).serialize();
        }

        boolean csvHeaders;
        headers = headers.toLowerCase();
        // checking for correct input for the hasHeader field
        if(headers.equals("true"))
            csvHeaders = true;
        else if(headers.equals("false"))
            csvHeaders = false;
        else {
            return new LoadCSVResponse("error_bad_request: header parameter must be true or false", url, filepath, headers).serialize();
        }

        try {
            this.activeCSV.setActiveCSV(new Searcher(filepath, csvHeaders, new StringListCreator()));
            return new LoadCSVResponse("success", url, filepath, headers).serialize();
        } catch(Exception e) {
            e.printStackTrace();
            return new LoadCSVResponse("error_datasource: file not found", url, filepath, headers).serialize();
        }
    }
}
