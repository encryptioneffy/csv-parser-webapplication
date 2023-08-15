import edu.brown.cs32.examples.moshiExample.CSV.Searching.Searcher;
import edu.brown.cs32.examples.moshiExample.CSV.Searching.StringListCreator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SearcherTest {

    @Test
    public void searchNotFound() {
        Searcher s = null;
        try {
            s = new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/ten-star.csv", true, new StringListCreator());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(new ArrayList<>(), s.search("Poop"));
    }

    @Test
    public void basicSearch() {
        Searcher s = null;
        try {
            s = new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/ten-star.csv", true, new StringListCreator());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<List<String>> expected =
                List.of(List.of("70667", "Proxima Centauri", "-0.47175", "-0.36132", "-1.15037"));
        assertEquals(expected, s.search("Proxima"));
    }

    @Test
    public void searchWrongCol() {
        Searcher s = null;
        try {
            s = new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/ten-star.csv", true, new StringListCreator());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            assertEquals(new ArrayList<>(), s.search("Proxima", "0"));
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void searchRightCol() {
        Searcher s = null;
        try {
            s = new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/ten-star.csv", true, new StringListCreator());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<List<String>> expected =
                List.of(List.of("70667", "Proxima Centauri", "-0.47175", "-0.36132", "-1.15037"));

        try {
            assertEquals(expected, s.search("Proxima", "1"));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void searchColName() {
        Searcher s = null;
        try {
            s = new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/ten-star.csv", true, new StringListCreator());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<List<String>> expected =
                List.of(List.of("70667", "Proxima Centauri", "-0.47175", "-0.36132", "-1.15037"));

        try {
            assertEquals(expected, s.search("Proxima", "propername"));
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void searchWithSpaces() {
        Searcher s = null;
        try {
            s = new Searcher("src/main/java/edu/brown/cs32/examples/moshiExample/data/ten-star.csv", true, new StringListCreator());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> e1 = List.of("71454", "Rigel Kentaurus B", "-0.50359", "-0.42128", "-1.1767");
        List<String> e2 = List.of("71457", "Rigel Kentaurus A", "-0.50362", "-0.42139", "-1.17665");
        List<List<String>> expected = List.of(e1, e2);
        assertEquals(expected, s.search("Rigel Kentaurus"));
    }
}
