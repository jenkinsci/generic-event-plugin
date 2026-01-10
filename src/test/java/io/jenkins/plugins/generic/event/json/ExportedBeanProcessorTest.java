package io.jenkins.plugins.generic.event.json;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExportedBeanProcessorTest {

    private JsonConfig jsonConfig;

    @BeforeEach
    void setUp() {
        jsonConfig = new EventJsonConfig();
    }

    @Test
    void serializeNullEntity() {
        Map<String, ExportedEntity> entityMap = new HashMap<>(1);
        entityMap.put("entity", null);
        String json = JSONObject.fromObject(entityMap, jsonConfig).toString();
        assertEquals("{\"entity\":null}", json);
    }

    @Test
    void shouldIgnoreURLandName() {
        ExportedEntity entity = new ExportedEntity(18, "fake/url", "fake name");
        String json = JSONObject.fromObject(entity, jsonConfig).toString();
        assertEquals("{\"_class\":\"io.jenkins.plugins.generic.event.json.ExportedBeanProcessorTest$ExportedEntity\",\"age\":18}", json);
    }

    @Test
    void shouldFallbackToNormalSerialization() {
        ExportedEntity entity = new ExportedEntity(-1, "fake/url", "fake name");
        JSONException jsonException = assertThrows(JSONException.class, () -> JSONObject.fromObject(entity, jsonConfig));
        assertEquals("Failed to serialize @Exported model", jsonException.getMessage());
        // Get the root cause of the problem. Exception chain: JSONException <- IOException <- IllegalStateException.
        Throwable realCause = jsonException.getCause().getCause().getCause();
        assertEquals(IllegalStateException.class, realCause.getClass());
        assertEquals("Age must not be less than 1", realCause.getMessage());
    }

    @ExportedBean
    public class ExportedEntity {

        private int age;

        private String url;

        private String name;

        public ExportedEntity(int age, String url, String name) {
            this.age = age;
            this.url = url;
            this.name = name;
        }

        @Exported
        public int getAge() {
            if (age <= 0) {
                throw new IllegalStateException("Age must not be less than 1");
            }
            return age;
        }

        public String getName() {
            return name;
        }

        @Exported
        public String getUrl() {
            return url;
        }
    }
}
