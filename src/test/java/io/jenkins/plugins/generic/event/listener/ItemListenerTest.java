package io.jenkins.plugins.generic.event.listener;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import hudson.ExtensionList;
import hudson.model.Items;
import hudson.model.TopLevelItem;
import io.jenkins.plugins.generic.event.Event;
import io.jenkins.plugins.generic.event.EventGlobalConfiguration;
import io.jenkins.plugins.generic.event.EventSender;
import jenkins.model.JenkinsLocationConfiguration;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.List;

import static net.sf.json.test.JSONAssert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
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
        config.setReceiver("http://localhost:8000");
//        config.setReceiver("http://localhost:5000/api/jenkins/event");
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

        File sourceDest = sourceFolder.getConfigFile().getFile();
        File targetDest = targetFolder.getConfigFile().getFile();
        System.out.println("Source Folder location: " + sourceDest);
        System.out.println("Target Folder location: " + targetDest);
        WorkflowJob pipelineProject = sourceFolder.createProject(WorkflowJob.class, "example-pipeline");
//        WorkflowMultiBranchProject project = folderProject.createProject(WorkflowMultiBranchProject.class, "my-multi-branch-pipeline");
//        WorkflowMultiBranchProject project = folderProject.createProject(WorkflowMultiBranchProject.class, "my-multi-branch-pipeline");
//        r.createFreeStyleProject("my-freestyle-project");

//        pipelineProject.move();
        File newBuildDir = targetFolder.getConfigFile().getFile();
        WorkflowJob pipeline = (WorkflowJob) sourceFolder.getItem("example-pipeline");
//        Collection<TopLevelItem> collection = sourceFolder.getItems();
//        WorkflowJob job = collection
        TopLevelItem tmp = sourceFolder.getItem("example-pipeline");

//        JenkinsRule.WebClient client = r.createWebClient();
        JSONObject json = new JSONObject();
        json.put("destination", "/target-folder");
        json.put("Jenkins-Crumb", "531b52a9598f8bc7f71817919c0173e93e1ff9ce8ddd812c5a3547634dc69a3e");
        JenkinsRule.JSONWebResponse response = r.postJSON("job/source-folder/job/example-pipeline/move/move", json);
        r.waitUntilNoActivity();
//        JSONObject json = {"destination": "/старая папка", "Jenkins-Crumb": "531b52a9598f8bc7f71817919c0173e93e1ff9ce8ddd812c5a3547634dc69a3e"}
//        client.goTo("job/target-folder/job/example-pipeline/move/move");





//        Items tmp = (Items) sourceFolder.getItem("example-pipeline");
//        tmp.move(tmp, targetFolder);
        pipelineProject.movedTo(targetFolder, pipeline, targetDest);
        pipelineProject.movedTo(targetFolder, pipeline, targetDest);
//        pipeline.movedTo("target-folder");
//        pipeline.renameTo("target-folder");
//        pipelineProject.renameTo("target-folder");
//        pipelineProject.movedTo(newBuildDir);

        r.waitUntilNoActivity();
        verify(mockSender, times(1)).send(any(Event.class));

        // todo move job to another folder
        // todo move folder to another folder
    }

    @Test
    public void renamePipeline() throws IOException, Exception {
        doNothing().when(mockSender).send(any());

        MockFolder sourceFolder = r.createFolder("source-folder");
        MockFolder targetFolder = r.createFolder("target-folder");

        File sourceDest = sourceFolder.getConfigFile().getFile();
        File targetDest = targetFolder.getConfigFile().getFile();
        System.out.println("Source Folder location: " + sourceDest);
        System.out.println("Target Folder location: " + targetDest);
        WorkflowJob pipelineProject = sourceFolder.createProject(WorkflowJob.class, "example-pipeline");

        WorkflowJob pipeline = (WorkflowJob) sourceFolder.getItem("example-pipeline");

        MockFolder f = sourceFolder;
        String oldName = f.getName();

        JenkinsRule.WebClient client = r.createWebClient();
        HtmlForm cfg = client.getPage(sourceFolder, "confirm-rename").getFormByName("config");
        cfg.getInputByName("newName").setValueAttribute("newName");
        for (HtmlForm form : r.submit(cfg).getForms()) {
            if (form.getActionAttribute().equals("confirmRename")) {
                r.submit(form);
                break;
            }
        }

        assertEquals("newName",f.getName());
        assertEquals("Some view",f.getDescription());
        assertNull(r.jenkins.getItem(oldName));
        assertSame(r.jenkins.getItem("newName"),f);






//        JenkinsRule.WebClient client = r.createWebClient();
//        JSONObject json = new JSONObject();
//        json.put("destination", "/target-folder");
//        json.put("Jenkins-Crumb", "531b52a9598f8bc7f71817919c0173e93e1ff9ce8ddd812c5a3547634dc69a3e");
//        JenkinsRule.JSONWebResponse response = r.postJSON("job/source-folder/job/example-pipeline/move/move", json);
//        r.waitUntilNoActivity();
//        JSONObject json = {"destination": "/старая папка", "Jenkins-Crumb": "531b52a9598f8bc7f71817919c0173e93e1ff9ce8ddd812c5a3547634dc69a3e"}
//        client.goTo("job/target-folder/job/example-pipeline/move/move");





//        Items tmp = (Items) sourceFolder.getItem("example-pipeline");
//        tmp.move(tmp, targetFolder);
//        pipelineProject.movedTo(targetFolder, pipeline, targetDest);
//        pipelineProject.movedTo(targetFolder, pipeline, targetDest);
//        pipeline.movedTo("target-folder");
//        pipeline.renameTo("target-folder");
//        pipelineProject.renameTo("target-folder");
//        pipelineProject.movedTo(newBuildDir);

        r.waitUntilNoActivity();
        verify(mockSender, times(1)).send(any(Event.class));

        // todo move job to another folder
        // todo move folder to another folder
    }
}
