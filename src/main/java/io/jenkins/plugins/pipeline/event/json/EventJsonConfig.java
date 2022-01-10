package io.jenkins.plugins.pipeline.event.json;

import java.time.Instant;

import org.kohsuke.stapler.export.ExportedBean;

import net.sf.json.JsonConfig;

/**
 * JSON configuration for event serialization.
 * 
 * @author johnniang
 */
public class EventJsonConfig extends JsonConfig {

    public EventJsonConfig() {
        registerJsonValueProcessor(Instant.class, new InstantProcessor());
        registerJsonBeanProcessor(ExportedBean.class, new ExportedBeanProcessor());
        setJsonBeanProcessorMatcher(new ExportedBeanProcessor.Matcher());
    }
}
