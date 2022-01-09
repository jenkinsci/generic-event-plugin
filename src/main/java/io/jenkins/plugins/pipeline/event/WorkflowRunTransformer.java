package io.jenkins.plugins.pipeline.event;

import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import jenkins.branch.MultiBranchProject;

public class WorkflowRunTransformer implements EventDataTransformer<WorkflowRun> {

    @Override
    public Object transform(WorkflowRun run) {
        WorkflowRunData data = new WorkflowRunData();
        data.setRaw(run);
        data.setNumber(run.getNumber());
        WorkflowJob project = run.getParent();

        data.setProjectName(project.getName());
        data.setParentFullName(project.getParent().getFullName());

        if (project.getParent() instanceof MultiBranchProject) {
            data.setMultiBranch(true);
            data.setRevision(project.getName());
            MultiBranchProject<?, ?> multiBranchProject = (MultiBranchProject<?, ?>) project.getParent();
            data.setProjectName(multiBranchProject.getName());
            data.setParentFullName(multiBranchProject.getParent().getFullName());
        }

        return data;
    }

}
