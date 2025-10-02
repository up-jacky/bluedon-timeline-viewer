package oauthServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static Map<String, Object> fromJson(String json) throws Exception {
        return MAPPER.readValue(json, Map.class);
    }
}
