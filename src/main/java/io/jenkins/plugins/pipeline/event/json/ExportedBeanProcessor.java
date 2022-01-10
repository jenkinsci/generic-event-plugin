package io.jenkins.plugins.pipeline.event.json;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.stapler.export.DataWriter;
import org.kohsuke.stapler.export.ExportedBean;
import org.kohsuke.stapler.export.Flavor;
import org.kohsuke.stapler.export.Model;
import org.kohsuke.stapler.export.ModelBuilder;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonBeanProcessor;
import net.sf.json.processors.JsonBeanProcessorMatcher;

/**
 * ExportedBeanProcessor is a JSON bean processor for object
 * annotated @ExportedBean.
 * 
 * @author johnniang
 */
public class ExportedBeanProcessor implements JsonBeanProcessor {

    private final Logger logger = Logger.getLogger(ExportedBeanProcessor.class.getName());

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public JSONObject processBean(Object value, JsonConfig jsonConfig) {
        final StringWriter stringWriter = new StringWriter();
        final DataWriter dataWriter;
        try {
            dataWriter = Flavor.JSON.createDataWriter(value, stringWriter);
            Model model = new ModelBuilder().get(value.getClass());
            model.writeTo(value, dataWriter);
            // return the JSON but as an object
            return JSONObject.fromObject(stringWriter.toString());
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to serialize @Exported data", e);
        }
        // fallback to original value
        return JSONObject.fromObject(value);
    }

    public static class Matcher extends JsonBeanProcessorMatcher {

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public Object getMatch(Class target, Set set) {
            if (target != null && target.isAnnotationPresent(ExportedBean.class)) {
                return ExportedBean.class;
            }
            return DEFAULT.getMatch(target, set);
        }

    }
}
