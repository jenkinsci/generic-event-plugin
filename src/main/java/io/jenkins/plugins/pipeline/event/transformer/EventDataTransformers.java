package io.jenkins.plugins.pipeline.event.transformer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum EventDataTransformers implements EventDataTransformer<Object> {

    INSTANCE;

    private final Map<Type, EventDataTransformer<?>> transformers;

    EventDataTransformers() {
        this.transformers = new HashMap<>();
        register(new WorkflowRunTransformer());
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

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object transform(Object data) {
        EventDataTransformer transformer = transformers.get(data.getClass());
        if (transformer == null) {
            // if not transformer found, just return the object only
            return data;
        }
        return transformer.transform(data);
    }

    private Type getActualArgumentType(EventDataTransformer<?> transformer) {
        return ((ParameterizedType) transformer.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

}
