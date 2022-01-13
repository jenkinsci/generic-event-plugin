package io.jenkins.plugins.pipeline.event;

import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import hudson.Extension;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

/**
 * WorkflowRunListener listens all WorkflowRun events, including onCompleted,
 * onDeleted, onFinalized, onInitialize and onStarted events.
 * 
 * @author johnniang
 */
@Extension
public class WorkflowRunListener extends RunListener<WorkflowRun> {

    private EventSender eventSender;

    public WorkflowRunListener() {
        // Set HTTP event sender by default
        this.setEventSender(new HttpEventSender());
    }

    public void setEventSender(EventSender eventSender) {
        this.eventSender = eventSender;
    }

    @Override
    public void onCompleted(WorkflowRun r, TaskListener listener) {
        eventSender.send(new Event.EventBuilder()
                .type("run.completed")
                .source(r.getParent().getUrl())
                .data(r)
                .build());
    }

    @Override
    public void onDeleted(WorkflowRun r) {
        eventSender.send(new Event.EventBuilder()
                .type("run.deleted")
                .source(r.getParent().getUrl())
                .data(r)
                .build());
    }

    @Override
    public void onFinalized(WorkflowRun r) {
        eventSender.send(new Event.EventBuilder()
                .type("run.finalized")
                .source(r.getParent().getUrl())
                .data(r)
                .build());
    }

    @Override
    public void onInitialize(WorkflowRun r) {
        eventSender.send(new Event.EventBuilder()
                .type("run.initialize")
                .source(r.getParent().getUrl())
                .data(r)
                .build());
    }

    @Override
    public void onStarted(WorkflowRun r, TaskListener listener) {
        eventSender.send(new Event.EventBuilder()
                .type("run.started")
                .source(r.getParent().getUrl())
                .data(r)
                .build());
    }

}
