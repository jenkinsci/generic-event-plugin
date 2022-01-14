package io.jenkins.plugins.pipeline.event.data;

import hudson.model.Hudson;
import io.jenkins.plugins.pipeline.event.data.WorkflowRunData.WorkflowRunTransformer;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

@Ignore
public class WorkflowRunDataTest {

    private WorkflowRun run;

    @Before
    public void setUp() throws IOException {
        Jenkins jenkins = Hudson.get();
        WorkflowJob workflowJob = new WorkflowJob(jenkins, "fake-workflow-job");
        run = new WorkflowRun(workflowJob);
    }

    @Test
    public void generalWorkflowRunTransform() {
        WorkflowRunTransformer transformer = new WorkflowRunTransformer();
        Object transformedRun = transformer.transform(run);
        Assert.assertSame(transformedRun.getClass(), WorkflowRunData.class);
    }

    @Test
    public void multiBranchWorkflowRunTransform() {

    }
}