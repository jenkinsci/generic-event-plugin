package io.jenkins.plugins.pipeline.event;

import java.time.Instant;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

/**
 * Event structure.
 */
public class Event {

    private String type;

    private String source;

    private String id;

    private Instant time;

    private String dataType;

    private Object data;

    public Event(EventBuilder builder) {
        this.type = builder.type;
        this.source = builder.source;
        this.id = builder.id;
        this.time = builder.time;
        this.dataType = builder.dataType;
        this.data = builder.data;
    }

    public static class EventBuilder {

        private String type;

        private String source;

        private String id;

        private Instant time;

        private String dataType;

        private Object data;

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

        public EventBuilder dataType(String dataType) {
            this.dataType = dataType;
            return this;
        }

        public EventBuilder data(Object data) {
            this.data = data;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Event other = (Event) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        if (dataType == null) {
            if (other.dataType != null)
                return false;
        } else if (!dataType.equals(other.dataType))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        } else if (!time.equals(other.time))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Event [data=" + data + ", dataType=" + dataType + ", id=" + id + ", source=" + source + ", time=" + time
                + ", type=" + type + "]";
    }
}
