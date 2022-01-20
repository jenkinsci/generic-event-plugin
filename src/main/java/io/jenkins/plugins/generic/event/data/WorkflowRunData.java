package io.jenkins.plugins.generic.event.data;

import io.jenkins.plugins.generic.event.transformer.EnhancedData;
import jenkins.branch.MultiBranchProject;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.kohsuke.stapler.export.Exported;

import java.util.Objects;

/**
 * WorkflowRun data structure. It contains the raw data of WorkflowRun.
 *
 * @author johnniang
 */
public class WorkflowRunData extends EnhancedData<WorkflowRun> {

    private String parentFullName;

    private String projectName;

    private boolean isMultiBranch;

    private final WorkflowRun run;

    public WorkflowRunData(WorkflowRun run) {
        this.run = run;
        WorkflowJob project = run.getParent();
        this.setProjectName(project.getName());
        this.setParentFullName(project.getParent().getFullName());
        if (project.getParent() instanceof MultiBranchProject) {
            this.setMultiBranch(true);
        }
    }

    @Exported(name = "_parentFullName")
    public String getParentFullName() {
        return parentFullName;
    }

    public void setParentFullName(String parentFullName) {
        this.parentFullName = parentFullName;
    }

    @Exported(name = "_projectName")
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Exported(name = "_multiBranch")
    public boolean isMultiBranch() {
        return isMultiBranch;
    }

    public void setMultiBranch(boolean isMultiBranch) {
        this.isMultiBranch = isMultiBranch;
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

    @Override
    public WorkflowRun getRaw() {
        return run;
    }

}
