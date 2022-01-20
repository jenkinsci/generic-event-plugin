package io.jenkins.plugins.generic.event.json;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class InstantProcessorTest {

    private JsonConfig jsonConfig;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(InstantProcessor.DATE_FORMAT_STRING)
            .withZone(ZoneId.systemDefault());

    private String format(TemporalAccessor temporal) {
        return formatter.format(temporal);
    }

    @Before
    public void setUp() {
        jsonConfig = new EventJsonConfig();
    }

    @Test
    public void serializeNullInstant() {
        InstantBean instant = new InstantBean(null);
        String instantJSON = JSONObject.fromObject(instant, jsonConfig).toString();
        Assert.assertEquals("{\"times\":[],\"time\":null}", instantJSON);
    }

    @Test
    public void shouldSerializeInstantCorrectly() {
        Instant time = Instant.parse("2022-01-14T06:49:30.00Z");
        InstantBean instant = new InstantBean(time);
        String instantJSON = JSONObject.fromObject(instant, jsonConfig).toString();

        Assert.assertEquals("{\"times\":[],\"time\":\""+format(time)+"\"}", instantJSON);
    }

    @Test
    public void shouldSerializeInstantArrayCorrectly() {
        Instant time = Instant.parse("2022-01-14T06:49:30.00Z");
        InstantBean instant = new InstantBean(time);
        instant.setTimes(new Instant[]{time});
        String instantJSON = JSONObject.fromObject(instant, jsonConfig).toString();
        Assert.assertEquals("{\"times\":[\""+format(time)+"\"],\"time\":\""+format(time)+"\"}", instantJSON);
    }

    public static class InstantBean {
        private final Instant time;

        private Instant[] times;

        public InstantBean(Instant time) {
            this.time = time;
        }

        public Instant getTime() {
            return time;
        }

        public void setTimes(Instant[] times) {
            this.times = times;
        }

        public Instant[] getTimes() {
            return times;
        }
    }
}
