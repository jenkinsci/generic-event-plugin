package io.jenkins.plugins.pipeline.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jenkinsci.plugins.workflow.flow.StepListener;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;

import hudson.Extension;

@Extension
public class WorkflowStepListener implements StepListener {

    private final Logger logger = Logger.getLogger(WorkflowStepListener.class.getName());

    @Override
    public void notifyOfNewStep(Step step, StepContext context) {
        logger.log(Level.INFO, () -> {
            return "Got a new step: " + step;
        });
    }
    
}
