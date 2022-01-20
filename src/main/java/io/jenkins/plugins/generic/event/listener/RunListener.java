package io.jenkins.plugins.generic.event.listener;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.generic.event.Event;
import io.jenkins.plugins.generic.event.EventSender;
import io.jenkins.plugins.generic.event.HttpEventSender;

@Extension
public class RunListener extends hudson.model.listeners.RunListener<Run<?, ?>> {

    private EventSender eventSender;

    public RunListener() {
        this.setEventSender(new HttpEventSender());
    }

    public void setEventSender(EventSender eventSender) {
        this.eventSender = eventSender;
    }

    @Override
    public void onCompleted(Run<?, ?> r, @NonNull TaskListener listener) {
        eventSender.send(new Event.EventBuilder()
                .type("run.completed")
                .source(r.getParent().getUrl())
                .data(r)
                .build());
    }


    @Override
    public void onDeleted(Run<?, ?> r) {
        eventSender.send(new Event.EventBuilder()
                .type("run.deleted")
                .source(r.getParent().getUrl())
                .data(r)
                .build());
    }

    @Override
    public void onFinalized(Run<?, ?> r) {
        eventSender.send(new Event.EventBuilder()
                .type("run.finalized")
                .source(r.getParent().getUrl())
                .data(r)
                .build());
    }

    @Override
    public void onInitialize(Run<?, ?> r) {
        eventSender.send(new Event.EventBuilder()
                .type("run.initialize")
                .source(r.getParent().getUrl())
                .data(r)
                .build());
    }

    @Override
    public void onStarted(Run<?, ?> r, TaskListener listener) {
        eventSender.send(new Event.EventBuilder()
                .type("run.started")
                .source(r.getParent().getUrl())
                .data(r)
                .build());
    }

}
