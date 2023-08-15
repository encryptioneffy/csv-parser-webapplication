package edu.brown.cs32.examples.moshiExample.server.handlers;

import edu.brown.cs32.examples.moshiExample.CSV.Searching.ColumnNotFoundException;
import edu.brown.cs32.examples.moshiExample.CSV.Searching.NoHeaderException;
import edu.brown.cs32.examples.moshiExample.CSV.Searching.Searcher;
import edu.brown.cs32.examples.moshiExample.server.ActiveCSVWrapper;
import spark.Request;
import spark.Response;
import spark.Route;
import edu.brown.cs32.examples.moshiExample.server.Responder.SearchCSVResponse;

import java.util.*;

/**
 * Handler class for the searchcsv API endpoint.
 */

public class SearchHandler implements Route {

    private ActiveCSVWrapper activeCSV;

    /**
     * Constructor accepts some shared active csv
     * @param csv the shared csv
     */
    public SearchHandler(ActiveCSVWrapper csv) {
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
        // getting our parameters
        Set<String> parameters = request.queryParams();
        Searcher currCSV = this.activeCSV.getActiveCSV();
        String url = request.url();
        String searchTerm = request.queryParams("search");
        String col = request.queryParams("column");

        // This request shouldn't be called if a csv has not been loaded, if it is give back an error message
        if(currCSV == null) {
            return new SearchCSVResponse("error_datasource: no CSV loaded", url, searchTerm, col, new ArrayList<>()).serialize();
        }

        if (parameters.size() > 2) {
            // error because it has too many arguments, we only expect a search term and column
            return new SearchCSVResponse("error_bad_json: too many parameters to search", url, searchTerm, col, new ArrayList<>()).serialize();
        } else if (parameters.size() < 1 || searchTerm == null) {
            // error because it has too few arguments, we expect at least a search term
            return new SearchCSVResponse("error_bad_request: no search parameter provided", url, searchTerm, col, new ArrayList<>()).serialize();
        }

        // if a column index or column identifier is provided:
        if(col != null) {
            try {
                // column is valid, successful search!
                return new SearchCSVResponse("success", url, searchTerm, col, currCSV.search(searchTerm, col)).serialize();
            } catch(IndexOutOfBoundsException e) {
                // column index provided is larger than the amount of columns in the csv
                return new SearchCSVResponse("error_bad_request: " + e.getMessage(), url, searchTerm, col, new ArrayList<>()).serialize();
            } catch(NoHeaderException e) {
                // column identifier provided to a csv with no column headers
                return new SearchCSVResponse("error_datasource: " + e.getMessage(), url, searchTerm, col, new ArrayList<>()).serialize();
            } catch(ColumnNotFoundException e) {
                // column identifier provided was not a valid header in the csv file
                return new SearchCSVResponse("error_bad_request: " + e.getMessage(), url, searchTerm, col, new ArrayList<>()).serialize();
            }
        }

        // well-formed search with no column identifier
        return new SearchCSVResponse("success", url, searchTerm, null, currCSV.search(searchTerm)).serialize();
    }
}
