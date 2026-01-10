package io.jenkins.plugins.generic.event.json;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InstantProcessorTest {

    private JsonConfig jsonConfig;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(InstantProcessor.DATE_FORMAT_STRING)
            .withZone(ZoneId.systemDefault());

    @BeforeEach
    void setUp() {
        jsonConfig = new EventJsonConfig();
    }

    @Test
    void serializeNullInstant() {
        InstantBean instant = new InstantBean(null);
        String instantJSON = JSONObject.fromObject(instant, jsonConfig).toString();
        assertEquals("{\"times\":[],\"time\":null}", instantJSON);
    }

    @Test
    void shouldSerializeInstantCorrectly() {
        Instant time = Instant.parse("2022-01-14T06:49:30.00Z");
        InstantBean instant = new InstantBean(time);
        String instantJSON = JSONObject.fromObject(instant, jsonConfig).toString();

        assertEquals("{\"times\":[],\"time\":\""+format(time)+"\"}", instantJSON);
    }

    @Test
    void shouldSerializeInstantArrayCorrectly() {
        Instant time = Instant.parse("2022-01-14T06:49:30.00Z");
        InstantBean instant = new InstantBean(time);
        instant.setTimes(new Instant[]{time});
        String instantJSON = JSONObject.fromObject(instant, jsonConfig).toString();
        assertEquals("{\"times\":[\""+format(time)+"\"],\"time\":\""+format(time)+"\"}", instantJSON);
    }

    private String format(TemporalAccessor temporal) {
        return formatter.format(temporal);
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
