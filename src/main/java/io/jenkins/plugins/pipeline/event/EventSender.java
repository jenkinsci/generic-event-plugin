package io.jenkins.plugins.pipeline.event;

import java.util.logging.Logger;

import io.jenkins.plugins.pipeline.event.transformer.EventDataTransformers;

/**
 * An interface for sending event by various methods, like asynchronization or
 * synchronization.
 */
public interface EventSender {

    void send(Event event);

    public static class NoopEventSender implements EventSender {

        private final Logger logger = Logger.getLogger(NoopEventSender.class.getName());

        @Override
        public void send(Event event) {
            // try to transform data
            Object transformedData = EventDataTransformers.INSTANCE.transform(event.getData());
            event.setData(transformedData);

            // Just log the event
            logger.info("Sent event: " + event.toString());
        }

    }
}
