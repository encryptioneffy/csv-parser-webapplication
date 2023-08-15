import java.io.IOException;
import java.util.Map;

import edu.brown.cs32.examples.moshiExample.server.JsonParser;
import org.junit.jupiter.api.Test;

public class JsonParserTest {
    public String json1 = "[{\"this\": \"is\", \"my\": {\"json\": \"OUTPUT1\", \"apple\": \"bat\"},  \"cat\": {\"dog\": \"eel\", \"fish\": { \"goat\": \"OUTPUT2\" },\"ick\": { \"june\": \"kin\" }}}]";
    public String json2 = "{\"red\":\"orange\",\"yellow\":\"green\",\"blue\":\"[success,OUTPUT]\",\"purple\":\"indigo\",\"pink\":\"lilac\"}";

    @Test
    public void test1() throws IOException {
        Map<String, Object> map = JsonParser.parse(json1);
        //should be OUTPUT
        System.out.println(map.get(".[0].my.json"));
        //should be OUTPUT2
        System.out.println(map.get(".[0].cat.fish.goat"));
    }

    @Test
    public void test2() throws IOException {
        Map<String, Object> map = JsonParser.parse(json2);

        //should be [success, OUTPUT]
        System.out.println(map.get(".blue"));

    }
}
