package io.jenkins.plugins.generic.event.json;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;

import net.sf.json.JSONException;
import org.kohsuke.stapler.export.DataWriter;
import org.kohsuke.stapler.export.ExportConfig;
import org.kohsuke.stapler.export.ExportInterceptor;
import org.kohsuke.stapler.export.ExportedBean;
import org.kohsuke.stapler.export.Flavor;
import org.kohsuke.stapler.export.Model;
import org.kohsuke.stapler.export.ModelBuilder;
import org.kohsuke.stapler.export.Property;

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

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public JSONObject processBean(Object value, JsonConfig jsonConfig) {
        final StringWriter stringWriter = new StringWriter();
        final DataWriter dataWriter;
        try {
            ExportConfig exportConfig = new ExportConfig().withExportInterceptor(new IgnoreURLExportInterceptor());
            dataWriter = Flavor.JSON.createDataWriter(value, stringWriter, exportConfig);
            Model model = new ModelBuilder().get(value.getClass());
            model.writeTo(value, dataWriter);
            // return the JSON but as an object
            return JSONObject.fromObject(stringWriter.toString());
        } catch (IOException e) {
            throw new JSONException("Failed to serialize @Exported model", e);
        }
    }

    public static class Matcher extends JsonBeanProcessorMatcher {

        @Override
        @SuppressWarnings({"unchecked" })
        public Object getMatch(Class target, Set set) {
            if (target != null && target.isAnnotationPresent(ExportedBean.class)) {
                return ExportedBean.class;
            }
            return DEFAULT.getMatch(target, set);
        }

    }

    public static class IgnoreURLExportInterceptor extends ExportInterceptor {

        @Override
        public Object getValue(Property property, Object model, ExportConfig config) throws IOException {
            if (property.name.equals("url")) {
                return SKIP;
            }
            return DEFAULT.getValue(property, model, config);
        }

    }
}
