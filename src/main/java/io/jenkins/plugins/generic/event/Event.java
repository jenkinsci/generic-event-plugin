package io.jenkins.plugins.generic.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

/**
 * Event structure.
 *
 * @author johnniang
 */
public class Event {

    private String type;

    private String source;

    private String url;

    private String id;

    private Instant time;

    private String dataType;

    private Object data;
    private MetaData metaData;

    public Event(EventBuilder builder) {
        this.type = builder.type;
        this.source = builder.source;
        this.url = builder.url;
        this.id = builder.id;
        this.time = builder.time;
        this.dataType = builder.dataType;
        this.data = builder.data;
        this.metaData = builder.metaData;
    }

    public static class EventBuilder {

        private String type;

        private String source;

        private String url;

        private String id;

        private Instant time;

        private String dataType;

        private Object data;

        private MetaData metaData;

        public EventBuilder() {
        }

        public EventBuilder type(String type) {
            this.type = type;
            return this;
        }

        public EventBuilder source(String source) {
            this.source = source;
            return this;
        }

        public EventBuilder url(String url) {
            this.url = url;
            return this;
        }

        public EventBuilder dataType(String dataType) {
            this.dataType = dataType;
            return this;
        }

        public EventBuilder data(Object data) {
            this.data = data;
            return this;
        }

        public EventBuilder metaData(MetaData metaData) {
            this.metaData = metaData;
            return this;
        }

        public Event build() {
            if (StringUtils.isBlank(dataType) && data != null) {
                dataType = data.getClass().getName();
            }
            id = UUID.randomUUID().toString();
            time = Instant.now();
            // TODO Validate other fields.
            return new Event(this);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(type, event.type)
                && Objects.equals(source, event.source)
                && Objects.equals(url, event.url)
                && Objects.equals(id, event.id)
                && Objects.equals(time, event.time)
                && Objects.equals(dataType, event.dataType)
                && Objects.equals(data, event.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, source, url, id, time, dataType, data);
    }

    public String getUrlData() {
        return "item.locationChanged".equals(type) ? ", metaData=" + metaData.toString() : ", url=" + url;
    }

    @Override
    public String toString() {
        return "Event [data=" + data + ", dataType=" + dataType + ", id=" + id +
                ", source=" + source + this.getUrlData() + ", time=" + time + ", type=" + type + "]";
    }
}
