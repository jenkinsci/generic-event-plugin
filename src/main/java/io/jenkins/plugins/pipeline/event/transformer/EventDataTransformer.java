package io.jenkins.plugins.pipeline.event.transformer;

public interface EventDataTransformer<T> {

    Object transform(T data);

}
