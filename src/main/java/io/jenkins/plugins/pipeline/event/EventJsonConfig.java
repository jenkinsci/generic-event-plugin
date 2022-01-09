package io.jenkins.plugins.pipeline.event;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.stapler.export.DataWriter;
import org.kohsuke.stapler.export.ExportedBean;
import org.kohsuke.stapler.export.Flavor;
import org.kohsuke.stapler.export.Model;
import org.kohsuke.stapler.export.ModelBuilder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.processors.JsonValueProcessorMatcher;

public class EventJsonConfig extends JsonConfig {

    private final Logger logger = Logger.getLogger(EventJsonConfig.class.getName());

    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public EventJsonConfig() {
        registerJsonValueProcessor(Instant.class, new JsonValueProcessor() {
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
        });

        registerJsonValueProcessor(ExportedBean.class, new JsonValueProcessor() {
            @Override
            public Object processArrayValue(Object value, JsonConfig jsonConfig) {
                return JSONArray.fromObject(processeExportedBean(value));
            }

            @Override
            public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
                return JSONObject.fromObject(processeExportedBean(value));
            }

            private Object processeExportedBean(Object value) {
                StringWriter stringWriter = new StringWriter();
                DataWriter dataWriter;
                try {
                    dataWriter = Flavor.JSON.createDataWriter(value, stringWriter);
                    Model model = new ModelBuilder().get(value.getClass());
                    model.writeTo(value, dataWriter);
                    // return the JSON string
                    return stringWriter.toString();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Failed to serialize @Exported data", e);
                }
                // fallback to original value
                return value;
            }
        });

        this.setJsonValueProcessorMatcher(new JsonValueProcessorMatcher() {
            @Override
            public Object getMatch(Class target, Set set) {
                if (target != null && target.getAnnotation(ExportedBean.class) != null) {
                    return ExportedBean.class;
                }
                // fallback to default matcher.
                return DEFAULT.getMatch(target, set);
            }
        });
    }
}
