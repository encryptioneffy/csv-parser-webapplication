package edu.brown.cs32.examples.moshiExample.server;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// inspired from dchen71 & jliu238 repo

public class JsonParser {
    //set up Moshi
    private static final Moshi moshi = new Moshi.Builder().build();
    private static final JsonAdapter<Map<String, Object>> adapter = moshi.adapter(
            Types.newParameterizedType(Map.class, String.class, Object.class, List.class));

    // identify json & call mapDfs
    public static Map<String, Object> parse(String json) throws IOException {
        if (json.startsWith("[")) {
            //needs to be a map
            json = "{\"\":"+json+"}";
        }
        Map<String, Object> map = adapter.fromJson(json);
        Map<String, Object> res = new HashMap<>();
        mapDfs(res, "", map);
        return res;
    }

    private static void mapDfs(Map<String, Object> outerMap, String currName, Map<String, Object> subMap) {
        for (Map.Entry<String, Object> sub : subMap.entrySet()) {
            String name = currName+"."+sub.getKey();
            if (sub.getValue() instanceof Map) {
                mapDfs(outerMap, name, ((Map<String, Object>) sub.getValue()));
            }
            else if (sub.getValue() instanceof List) {
                listDfs(outerMap, name, ((List<Object>) sub.getValue()));
            } else {
                outerMap.put(name, sub.getValue());
            }
        }
    }

    private static void listDfs(Map<String, Object> outerMap, String currName, List<Object> subList) {
        for (int i = 0; i < subList.size(); i++) {
            Object obj = subList.get(i);
            String name = currName+"["+i+"]";
            if (obj instanceof Map) {
                mapDfs(outerMap, name, ((Map<String, Object>) obj));
            }
            else if (obj instanceof List) {
                listDfs(outerMap, name, ((List<Object>) obj));
            } else {
                outerMap.put(name, obj);
            }
        }
    }
}
