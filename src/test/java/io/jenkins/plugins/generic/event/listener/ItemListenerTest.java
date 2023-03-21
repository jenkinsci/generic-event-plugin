package io.jenkins.plugins.generic.event.listener;

import hudson.ExtensionList;
import hudson.model.Job;
import io.jenkins.plugins.generic.event.Event;
import io.jenkins.plugins.generic.event.EventGlobalConfiguration;
import io.jenkins.plugins.generic.event.EventSender;
import jenkins.model.JenkinsLocationConfiguration;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.kohsuke.stapler.Stapler;

import java.io.File;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemListenerTest {

    @Rule
    public final JenkinsRule r = new JenkinsRule();

    @Mock
    EventSender mockSender;

    @Before
    public void setUp() {
        EventGlobalConfiguration config = EventGlobalConfiguration.get();
        config.setReceiver("http://localhost:5000/api/jenkins/event");
        JenkinsLocationConfiguration.get().setUrl(null);

        MockitoAnnotations.openMocks(this);
        ExtensionList<GenericEventItemListener> listeners = ExtensionList.lookup(GenericEventItemListener.class);
        listeners.forEach((listener) -> listener.setEventSender(mockSender));
    }

    @Test
    public void movePipelineToAnotherFolder() throws IOException, Exception {
        doNothing().when(mockSender).send(any());

        MockFolder sourceFolder = r.createFolder("source-folder");
        MockFolder targetFolder = r.createFolder("target-folder");

        System.out.println("Source Folder location: " + sourceFolder.getConfigFile().getFile());
        System.out.println("Target Folder location: " + targetFolder.getConfigFile().getFile());
        WorkflowJob pipelineProject = sourceFolder.createProject(WorkflowJob.class, "example-pipeline");
//        WorkflowMultiBranchProject project = folderProject.createProject(WorkflowMultiBranchProject.class, "my-multi-branch-pipeline");
//        WorkflowMultiBranchProject project = folderProject.createProject(WorkflowMultiBranchProject.class, "my-multi-branch-pipeline");
//        r.createFreeStyleProject("my-freestyle-project");

//        pipelineProject.move();
        File newBuildDir = targetFolder.getConfigFile().getFile();
        pipelineProject.renameTo("target-folder");
//        pipelineProject.movedTo(newBuildDir);

        r.waitUntilNoActivity();
        verify(mockSender, times(1)).send(any(Event.class));

        // todo move job to another folder
        // todo move folder to another folder
    }
}
