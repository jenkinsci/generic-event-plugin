package io.jenkins.plugins.pipeline.event.data;

import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import io.jenkins.plugins.pipeline.event.transformer.EventDataTransformer;
import jenkins.branch.MultiBranchProject;

import java.util.Objects;

/**
 * WorkflowRun data structure. It contains the raw data of WorkflowRun.
 *
 * @author johnniang
 */
public class WorkflowRunData {

    private String parentFullName;

    private String projectName;

    private boolean isMultiBranch;

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

    public boolean isMultiBranch() {
        return isMultiBranch;
    }

    public void setMultiBranch(boolean isMultiBranch) {
        this.isMultiBranch = isMultiBranch;
    }

    public WorkflowRun getRun() {
        return run;
    }

    public void setRun(WorkflowRun run) {
        this.run = run;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkflowRunData that = (WorkflowRunData) o;
        return isMultiBranch == that.isMultiBranch && Objects.equals(parentFullName, that.parentFullName)
                && Objects.equals(projectName, that.projectName) && Objects.equals(run, that.run);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentFullName, projectName, isMultiBranch, run);
    }

    @Override
    public String toString() {
        return "WorkflowRunData{" +
                "parentFullName='" + parentFullName + '\'' +
                ", projectName='" + projectName + '\'' +
                ", isMultiBranch=" + isMultiBranch +
                ", run=" + run +
                '}';
    }

    public static class WorkflowRunTransformer implements EventDataTransformer<WorkflowRun> {

        @Override
        public Object transform(WorkflowRun run) {
            WorkflowRunData data = new WorkflowRunData();
            data.setRun(run);
            WorkflowJob project = run.getParent();
            data.setProjectName(project.getName());
            data.setParentFullName(project.getParent().getFullName());
            if (project.getParent() instanceof MultiBranchProject) {
                data.setMultiBranch(true);
            }
            return data;
        }

    }

}
