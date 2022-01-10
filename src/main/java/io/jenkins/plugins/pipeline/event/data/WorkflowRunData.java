package io.jenkins.plugins.pipeline.event.data;

import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import io.jenkins.plugins.pipeline.event.transformer.EventDataTransformer;
import jenkins.branch.MultiBranchProject;

/**
 * WorkflowRun data structure. It contains the raw data of WorkflowRun.
 * 
 * @author johnniang
 */
public class WorkflowRunData {

    private String parentFullName;

    private String projectName;

    private int number;

    private boolean isMultiBranch;

    private String revision;

    private WorkflowRun run;

    public String getParentFullName() {
        return parentFullName;
    }

    public void setParentFullName(String parentFullName) {
        this.parentFullName = parentFullName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isMultiBranch() {
        return isMultiBranch;
    }

    public void setMultiBranch(boolean isMultiBranch) {
        this.isMultiBranch = isMultiBranch;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public WorkflowRun getRaw() {
        return run;
    }

    public void setRaw(WorkflowRun raw) {
        this.run = raw;
    }

    public static class WorkflowRunTransformer implements EventDataTransformer<WorkflowRun> {

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

}
