package io.jenkins.plugins.pipeline.event;

import org.jenkinsci.plugins.workflow.job.WorkflowRun;

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

    private WorkflowRun raw;

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
        return raw;
    }

    public void setRaw(WorkflowRun raw) {
        this.raw = raw;
    }

}
