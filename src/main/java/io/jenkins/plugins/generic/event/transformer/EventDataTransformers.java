package io.jenkins.plugins.generic.event.transformer;

import io.jenkins.plugins.generic.event.Event;
import io.jenkins.plugins.generic.event.data.WorkflowRunData;
import net.jodah.typetools.TypeResolver;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * EventDataTransformers is a set of event data transformers and is responsible
 * for picking a proper transformer to transform an object. It also supports
 * transformer registering and unregistering.
 * <p>
 * It's lazy-load and a singleton type.
 *
 * @author johnniang
 */
public enum EventDataTransformers {

    INSTANCE;

    private final Map<Type, EventDataTransformer<?>> transformers;

    EventDataTransformers() {
        this.transformers = new HashMap<>();
        register((EventDataTransformer<WorkflowRun>) WorkflowRunData::new);
        // TODO register other event data transformers.
    }

    public void register(EventDataTransformer<?> transformer) {
        transformers.put(getActualArgumentType(transformer), transformer);
    }

    public void unregister(EventDataTransformer<?> transformer) {
        transformers.remove(getActualArgumentType(transformer), transformer);
    }

    public Collection<EventDataTransformer<?>> getAllTransformers() {
        return Collections.unmodifiableCollection(transformers.values());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void transform(Event event) {
        if (event == null || event.getData() == null) {
            return;
        }
        EventDataTransformer transformer = transformers.get(event.getData().getClass());
        if (transformer == null) {
            // if not transformer found, nothing need to do
            return;
        }
        Object transformedData = transformer.transform(event.getData());
        event.setData(transformedData);
    }

    private Type getActualArgumentType(EventDataTransformer<?> transformer) {
        return TypeResolver.resolveRawArgument(EventDataTransformer.class, transformer.getClass());
    }

}
