package edu.brown.cs32.examples.moshiExample.server;
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
//import java.util.Set;
import edu.brown.cs32.examples.moshiExample.CSV.Searching.Star;

import java.util.concurrent.ThreadLocalRandom;
public class FuzzTestHelper {

    //code adapted from tim nelson's csv repo
    public static String getRandomStringBounded(int first, int last) {
        final ThreadLocalRandom r = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder();
        for(int iCount=0;iCount<r.nextInt(1,10);iCount++) {
            // upper-bound is exclusive
            int code = r.nextInt(first, last+1);
            sb.append((char) code);
        }
        return sb.toString();
    }

    public static int getRandomIntBounded(int first, int last) {
        final ThreadLocalRandom r = ThreadLocalRandom.current();
        return r.nextInt(first, last+1);
    }

}
