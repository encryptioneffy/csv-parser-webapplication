package edu.brown.cs32.examples.moshiExample.server.handlers;

import edu.brown.cs32.examples.moshiExample.CSV.Searching.Searcher;
import edu.brown.cs32.examples.moshiExample.server.ActiveCSVWrapper;
import spark.Request;
import spark.Response;
import spark.Route;
import edu.brown.cs32.examples.moshiExample.server.Responder.ViewCSVResponse;

import java.util.*;

/**
 * Handler class for the viewcsv API endpoint.
 */
public class ViewHandler implements Route {
    private ActiveCSVWrapper activeCSV;

    /**
     * Constructor accepts some shared active csv
     * @param csv the shared csv
     */
    public ViewHandler(ActiveCSVWrapper csv) {
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

        // The set of parameters passed in the request
        Set<String> parameters = request.queryParams();
        String url = request.url();
        Searcher currCSV = this.activeCSV.getActiveCSV();

        // This request shouldn't have any parameters, if it does give back an error message
        if (parameters.size() != 0) {
            return new ViewCSVResponse("error_bad_json: too many parameters to view", url, new ArrayList<>()).serialize();
        }

        //This request shouldn't be called if a csv has not been loaded, if it is give back an error message
        if(currCSV == null) {
            return new ViewCSVResponse("error_datasource: no CSV loaded", url, new ArrayList<>()).serialize();
        }

        return new ViewCSVResponse("success", url, currCSV.getCSV().getParsedCsv()).serialize();
    }
}
