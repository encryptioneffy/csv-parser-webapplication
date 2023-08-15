package edu.brown.cs32.examples.moshiExample.server;

import edu.brown.cs32.examples.moshiExample.CSV.Searching.Searcher;

/**
 * Class for maintaining the shared csv state
 */
public class ActiveCSVWrapper {
    private Searcher activeCSV;

    /**
     * Constructor for the csv container
     */
    public ActiveCSVWrapper() {
        this.activeCSV = null;
    }

    /**
     * Get the shared active Searcher
     * @return
     */
    public Searcher getActiveCSV() {
        return this.activeCSV;
    }

    /**
     * Set the active Searcher
     * @param s the Searcher to set as the active csv
     */
    public void setActiveCSV(Searcher s) {
        this.activeCSV = s;
    }
}
