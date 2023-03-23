package io.jenkins.plugins.generic.event.listener;

import com.cloudbees.hudson.plugins.folder.Folder;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import hudson.ExtensionList;
import hudson.model.TopLevelItem;
import io.jenkins.plugins.generic.event.Event;
import io.jenkins.plugins.generic.event.EventGlobalConfiguration;
import io.jenkins.plugins.generic.event.EventSender;
import jenkins.model.JenkinsLocationConfiguration;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import static net.sf.json.test.JSONAssert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemListenerNewTest {

    @Rule public final JenkinsRule r = new JenkinsRule();

    @Mock
    EventSender mockSender;

    @Before
    public void setUp() {
        EventGlobalConfiguration config = EventGlobalConfiguration.get();
        config.setReceiver("http://localhost:5000/base");
//        JenkinsLocationConfiguration.get().setUrl(null);
//
        MockitoAnnotations.openMocks(this);
        ExtensionList<GenericEventItemListener> listeners = ExtensionList.lookup(GenericEventItemListener.class);
        listeners.forEach((listener) -> listener.setEventSender(mockSender));
    }

    @Test public void rename() throws Exception {
        Folder f = createFolder();
        f.setDescription("Some view");
        assertSame(r.jenkins.getItem("folder0"), f);

        r.waitUntilNoActivity();
        verify(mockSender, times(4)).send(any(Event.class));
        String oldName = f.getName();

        HtmlForm cfg = r.createWebClient().getPage(f, "confirm-rename").getFormByName("config");
        cfg.getInputByName("newName").setValueAttribute("newName");
        for (HtmlForm form : r.submit(cfg).getForms()) {
            if (form.getActionAttribute().equals("confirmRename")) {
                r.submit(form);
                break;
            }
        }

        Assert.assertEquals("newName",f.getName());
        Assert.assertEquals("Some view",f.getDescription());
        assertNull(r.jenkins.getItem(oldName));
        assertSame(r.jenkins.getItem("newName"),f);

        verify(mockSender, times(5)).send(any(Event.class));
    }


    private Folder createFolder() throws IOException {
        return r.jenkins.createProject(Folder.class, "folder" + r.jenkins.getItems().size());
    }



    // todo move job to another folder
        // todo move folder to another folder

}
