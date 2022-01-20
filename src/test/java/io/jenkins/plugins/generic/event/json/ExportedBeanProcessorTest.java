package io.jenkins.plugins.generic.event.json;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.HashMap;
import java.util.Map;

public class ExportedBeanProcessorTest {

    private JsonConfig jsonConfig;

    @Before
    public void setUp() {
        jsonConfig = new EventJsonConfig();
    }

    @Test
    public void serializeNullEntity() {
        Map<String, ExportedEntity> entityMap = new HashMap<>(1);
        entityMap.put("entity", null);
        String json = JSONObject.fromObject(entityMap, jsonConfig).toString();
        Assert.assertEquals("{\"entity\":null}", json);
    }

    @Test
    public void shouldIgnoreURLandName() {
        ExportedEntity entity = new ExportedEntity(18, "fake/url", "fake name");
        String json = JSONObject.fromObject(entity, jsonConfig).toString();
        Assert.assertEquals("{\"_class\":\"io.jenkins.plugins.generic.event.json.ExportedBeanProcessorTest$ExportedEntity\",\"age\":18}", json);
    }

    @Test
    public void shouldFallbackToNormalSerialization() {
        ExportedEntity entity = new ExportedEntity(-1, "fake/url", "fake name");
        JSONException jsonException = Assert.assertThrows(JSONException.class, () -> JSONObject.fromObject(entity, jsonConfig));
        Assert.assertEquals("Failed to serialize @Exported model", jsonException.getMessage());
        // Get the root cause of the problem. Exception chain: JSONException <- IOException <- IllegalStateException.
        Throwable realCause = jsonException.getCause().getCause().getCause();
        Assert.assertEquals(IllegalStateException.class, realCause.getClass());
        Assert.assertEquals("Age must not be less than 1", realCause.getMessage());
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
