package io.jenkins.plugins.pipeline.event;

public interface EventDataTransformer<T> {

    Object transform(T data);

}
