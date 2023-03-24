package io.jenkins.plugins.generic.event.listener;

import com.cloudbees.hudson.plugins.folder.Folder;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import hudson.ExtensionList;
import hudson.model.FreeStyleProject;
import hudson.model.TopLevelItem;
import io.jenkins.plugins.generic.event.Event;
import io.jenkins.plugins.generic.event.EventGlobalConfiguration;
import io.jenkins.plugins.generic.event.EventSender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

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

    @Test public void renameJob() throws Exception {
        Folder f = createFolder();
        f.setDescription("Some view");
        TopLevelItem folderItem = r.jenkins.getItem("folder0");
        assertSame(r.jenkins.getItem("folder0"), f);

        r.waitUntilNoActivity();
        verify(mockSender, times(4)).send(any(Event.class));

        FreeStyleProject child = f.createProject(FreeStyleProject.class, "old-job-name");
        String oldName = child.getName();

        r.waitUntilNoActivity();
        verify(mockSender, times(5)).send(any(Event.class));

        HtmlForm cfg = r.createWebClient().getPage(child, "confirm-rename").getFormByName("config");
        cfg.getInputByName("newName").setValueAttribute("new-job-name");
        for (HtmlForm form : r.submit(cfg).getForms()) {
            if (form.getActionAttribute().equals("confirmRename")) {
                r.submit(form);
                break;
            }
        }

        r.waitUntilNoActivity();

        Assert.assertEquals("new-job-name",child.getName());
        assertNull(r.jenkins.getItemByFullName("folder0/old-job-name"));
        assertSame(r.jenkins.getItemByFullName("folder0/new-job-name"), child);
        verify(mockSender, times(7)).send(any(Event.class));
    }

    @Test public void renameFolder() throws Exception {
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

    @Test public void moveJob() throws Exception {

        Folder f1 = createFolder();
        Folder f2 = createFolder();
        f1.setDescription("Some view");
        TopLevelItem folderItem = r.jenkins.getItem("folder0");
        assertSame(r.jenkins.getItem("folder1"), f1);
        assertSame(r.jenkins.getItem("folder2"), f2);

        r.waitUntilNoActivity();
        verify(mockSender, times(4)).send(any(Event.class));

        FreeStyleProject child = f1.createProject(FreeStyleProject.class, "freestyle-job");
        String oldName = child.getName();

        r.waitUntilNoActivity();
        verify(mockSender, times(5)).send(any(Event.class));

        List<HtmlForm> forms = r.createWebClient().getPage(child, "move/").getForms();

        for (HtmlForm form: forms) {
            if (form.getActionAttribute().equals("move")) {
                // todo ДОДЕЛАТЬ
                form.getSelectByName("destination").getOptions();
                form.getSelectByName("destination").setSelectedAttribute("target-folder", true);
                form.getInputByName("destination").setValueAttribute("target-folder");
                r.submit(form);
                break;
            }
        }

//        HtmlForm cfg = r.createWebClient().getPage(child, "move/").getForms();
//        HtmlForm cfg = r.createWebClient().getPage(child, "move/").getFormByName("config");
//        cfg.getInputByName("destination").setValueAttribute("target-folder");
//        for (HtmlForm form : r.submit(cfg).getForms()) {
//            if (form.getActionAttribute().equals("move")) {
//                r.submit(form);
//                break;
//            }
//        }

        r.waitUntilNoActivity();

        Assert.assertEquals("new-job-name",child.getName());
        assertNull(r.jenkins.getItemByFullName("folder0/old-job-name"));
        assertSame(r.jenkins.getItemByFullName("folder0/new-job-name"), child);
        verify(mockSender, times(7)).send(any(Event.class));
    }

    private Folder createFolder() throws IOException {
        return r.jenkins.createProject(Folder.class, "folder" + r.jenkins.getItems().size() + 1);
    }

    // todo move job to another folder
    // todo move folder to another folder

}
