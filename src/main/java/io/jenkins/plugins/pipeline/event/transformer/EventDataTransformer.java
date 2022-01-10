package io.jenkins.plugins.pipeline.event.transformer;

/**
 * This interface is responsible for transforming object with specific type to
 * another structure.
 * 
 * @author johnniang
 */
public interface EventDataTransformer<T> {

    /**
     * Transform object with specific type to another structure.
     * 
     * @param data object data to be transformed.
     * @return target object.
     */
    Object transform(T data);

}
