package io.jenkins.plugins.pipeline.event.transformer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.jenkins.plugins.pipeline.event.Event;
import io.jenkins.plugins.pipeline.event.data.WorkflowRunData;

/**
 * EventDataTransformers is a set of event data transformers and is responsible
 * for picking a proper transformer to transform an object. It also supports
 * transformer registering and unregistering.
 * 
 * It's lazy-load and a singleton type.
 * 
 * @author johnniang
 * 
 */
public enum EventDataTransformers {

    INSTANCE;

    private final Map<Type, EventDataTransformer<?>> transformers;

    EventDataTransformers() {
        this.transformers = new HashMap<>();
        register(new WorkflowRunData.WorkflowRunTransformer());
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
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
        if (transformedData != null) {
            event.setDataType(transformedData.getClass().getName());
        }
    }

    private Type getActualArgumentType(EventDataTransformer<?> transformer) {
        return ((ParameterizedType) transformer.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

}
