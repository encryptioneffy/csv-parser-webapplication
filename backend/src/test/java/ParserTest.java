import edu.brown.cs32.examples.moshiExample.CSV.Parsing.Parser;
import edu.brown.cs32.examples.moshiExample.CSV.Searching.Star;
import edu.brown.cs32.examples.moshiExample.CSV.Searching.StarCreator;
import edu.brown.cs32.examples.moshiExample.CSV.Searching.StringListCreator;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {

  @Test // empty csv file
  public void emptyParseTest() {
    Reader input = new StringReader("");
    Parser<List<String>> parser = new Parser(input, new StringListCreator(), false);
    List<String> expected = List.of();
    assertEquals(expected, parser.parse());
  }

  @Test // csv one row + no headers
  public void oneRow() {
    Reader input = new StringReader("0,Sol,0,0");
    Parser<List<String>> parser = new Parser(input, new StringListCreator(), false);
    List<List<String>> expected = List.of(List.of("0", "Sol", "0", "0"));
    parser.parse();
    assertEquals(expected, parser.getParsedCsv());
    String[] empty = {};
    assertArrayEquals(empty, parser.getHeaders());
  }

  @Test // csv with headers
  public void csvWithHead() throws FileNotFoundException {
    Reader input = new FileReader("src/main/java/edu/brown/cs32/examples/moshiExample/data/ten-star.csv");
    Parser<List<String>> parser = new Parser(input, new StringListCreator(), true);
    List<String> row1 = List.of("0", "Sol", "0", "0", "0");
    List<String> row2 = List.of("1", "", "282.43485", "0.00449", "5.36884");
    List<String> row3 = List.of("2", "", "43.04329", "0.00285", "-15.24144");
    List<String> row4 = List.of("3", "", "277.11358", "0.02422", "223.27753");
    List<String> row5 = List.of("3759", "96 G. Psc", "7.26388", "1.55643", "0.68697");
    List<String> row6 = List.of("70667", "Proxima Centauri", "-0.47175", "-0.36132", "-1.15037");
    List<String> row7 = List.of("71454", "Rigel Kentaurus B", "-0.50359", "-0.42128", "-1.1767");
    List<String> row8 = List.of("71457", "Rigel Kentaurus A", "-0.50362", "-0.42139", "-1.17665");
    List<String> row9 = List.of("87666", "Barnard's Star", "-0.01729", "-1.81533", "0.14824");
    List<String> row10 = List.of("118721", "", "-2.28262", "0.64697", "0.29354");
    List<List<String>> expected =
        List.of(row1, row2, row3, row4, row5, row6, row7, row8, row9, row10);
    String[] e = {"starid", "propername", "x", "y", "z"};
    assertEquals(expected, parser.parse());
    assertArrayEquals(parser.getHeaders(), e);
  }

  @Test
  public void csvParseStarObj() throws FileNotFoundException {
    Reader input = new FileReader("src/main/java/edu/brown/cs32/examples/moshiExample/data/ten-star.csv");
    Parser<Star> parser = new Parser(input, new StarCreator(), true);
    Star row1 = new Star("0", "Sol", "0", "0", "0");
    Star row2 = new Star("1", "", "282.43485", "0.00449", "5.36884");
    Star row3 = new Star("2", "", "43.04329", "0.00285", "-15.24144");
    Star row4 = new Star("3", "", "277.11358", "0.02422", "223.27753");
    Star row5 = new Star("3759", "96 G. Psc", "7.26388", "1.55643", "0.68697");
    Star row6 = new Star("70667", "Proxima Centauri", "-0.47175", "-0.36132", "-1.15037");
    Star row7 = new Star("71454", "Rigel Kentaurus B", "-0.50359", "-0.42128", "-1.1767");
    Star row8 = new Star("71457", "Rigel Kentaurus A", "-0.50362", "-0.42139", "-1.17665");
    Star row9 = new Star("87666", "Barnard's Star", "-0.01729", "-1.81533", "0.14824");
    Star row10 = new Star("118721", "", "-2.28262", "0.64697", "0.29354");
    List<Star> expected = List.of(row1, row2, row3, row4, row5, row6, row7, row8, row9, row10);
    parser.parse();
    for (int i = 0; i < 10; i++) {
      assertEquals(expected.get(i).equals(parser.getParsedCsv().get(i)), true);
    }
  }
}
