package io.jenkins.plugins.pipeline.event.json;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * InstantProcessor is a JSON value processor for instant type.
 * 
 * @author johnniang
 */
public class InstantProcessor implements JsonValueProcessor {

    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_STRING)
            .withZone(ZoneId.systemDefault());

    @Override
    public Object processArrayValue(Object value, JsonConfig jsonConfig) {
        return formatter.format((Instant) value);
    }

    @Override
    public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
        return processArrayValue(value, jsonConfig);
    }
}
