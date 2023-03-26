package io.jenkins.plugins.generic.event.listener;

import com.cloudbees.hudson.plugins.folder.Folder;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import hudson.ExtensionList;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import hudson.model.Items;
import hudson.model.TopLevelItem;
import hudson.model.listeners.ItemListener;
import io.jenkins.plugins.generic.event.Event;
import io.jenkins.plugins.generic.event.EventGlobalConfiguration;
import io.jenkins.plugins.generic.event.EventSender;
import io.jenkins.plugins.generic.event.HttpEventSender;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockFolder;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.mockito.*;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemListenerNewTest {

    @Rule public JenkinsRule r = new JenkinsRule();

    @Mock
    EventSender mockSender;

    @Mock
    Event ev1;

    @Mock
    Event ev2;

    @Spy
    EventSender mockitoSender;

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

        // todo Переделать на renameTo
        // https://github.com/beka227/mp3/blob/db6c0d8c7c377ef8446e79f2b81ac936c56c15fb/jenkins/test/src/test/java/org/jvnet/hudson/test/MockFolderTest.java
        HtmlForm cfg = r.createWebClient().getPage(child, "confirm-rename").getFormByName("config");
        cfg.getInputByName("newName").setValueAttribute("new-job-name");
        for (HtmlForm form : r.submit(cfg).getForms()) {
            if (form.getActionAttribute().equals("confirmRename")) {
                r.submit(form);
                break;
            }
        }

        r.waitUntilNoActivity();

        assertEquals("new-job-name",child.getName());
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

        assertEquals("newName",f.getName());
        assertEquals("Some view",f.getDescription());
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
        verify(mockSender, times(6)).send(any(Event.class));

        FreeStyleProject child = f1.createProject(FreeStyleProject.class, "freestyle-job");
        String oldName = child.getName();

        r.waitUntilNoActivity();
        verify(mockSender, times(7)).send(any(Event.class));

//        r.jenkins.setCrumbIssuer(null);

//        JenkinsRule.JSONWebResponse response;
//        MyJsonObject objectToSend = new MyJsonObject("/folder2");
//        response = r.postJSON( "job/folder1/job/freestyle-job/move/move", JSONObject.fromObject(objectToSend));



        JSONObject json = new JSONObject();
        json.put("destination", "/folder2");
        json.put("Jenkins-Crumb", "test");

//        JenkinsRule.JSONWebResponse response = r.postJSON("job/folder1/job/freestyle-job/move/move", json);

//        JenkinsRule.JSONWebResponse response = r.postJSON("job/folder1/job/freestyle-job/move/move", json);
        URL apiURL = new URL(r.jenkins.getRootUrl().toString() + f1.getUrl().toString() + "job/freestyle-job/move/move");
//        WebRequest request = new WebRequest(apiURL, HttpMethod.GET);
//        request.setRequestBody();
//        request.setEncodingType(null);
//        request.set("Content-Type", "application/json; charset=UTF-8");

        WebRequest request = new WebRequest(apiURL, HttpMethod.POST);
        request.setRequestBody(json.toString());
        request.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded");
        WebResponse response = r.createWebClient().getPage(request).getWebResponse();
        // todo Сделать POST в лоб http://localhost:8080/jenkins/job/folder2/job/freestyle-job/move/move

        // todo doesn't work
//        List<HtmlForm> forms = r.createWebClient().getPage(child, "move/").getForms();
//        for (HtmlForm form: forms) {
//            if (form.getActionAttribute().equals("move")) {
//                form.getSelectByName("destination").getOptionByValue("/folder2").setAttribute("selected", "yes");
//                form.getSelectByName("destination").setSelectedAttribute("selected", true);
//                r.submit(form);
//                break;
//            }
//        }


        r.waitUntilNoActivity();

        assertEquals("new-job-name",child.getName());
        assertNull(r.jenkins.getItemByFullName("folder0/old-job-name"));
        assertSame(r.jenkins.getItemByFullName("folder0/new-job-name"), child);
        verify(mockSender, times(7)).send(any(Event.class));
    }

    @Test public void moveFreeStyleJob() throws Exception {

        MockFolder folder1 = r.createFolder("folder1");
        MockFolder folder2 = r.createFolder("folder2");
        FreeStyleProject project = folder1.createProject(FreeStyleProject.class, "freestyle-job");
        assertNews("created=folder1 created=folder2 created=folder1/freestyle-job");
        assertSame(r.jenkins.getItemByFullName("folder1/freestyle-job"), project);
        reset(mockSender);

        Items.move(project, folder2);
        assertNews("moved=folder2/freestyle-job;from=folder1/freestyle-job");
        verify(mockSender, times(1)).send(any(Event.class));
        assertNull(r.jenkins.getItemByFullName("folder1/freestyle-job"));
        assertSame(r.jenkins.getItemByFullName("folder2/freestyle-job"), project);
    }

    @Test public void moveFolder() throws Exception {

        MockFolder folder1 = r.createFolder("folder1");
        MockFolder folder2 = r.createFolder("folder2");
        MockFolder subFolder = folder1.createProject(MockFolder.class, "subfolder");
        assertNews("created=folder1 created=folder2 created=folder1/subfolder");
        assertSame(r.jenkins.getItemByFullName("folder1/subfolder"), subFolder);

        Items.move(subFolder, folder2);
        assertNews("moved=folder2/subfolder;from=folder1/subfolder");
        verify(mockSender, times(4)).send(any(Event.class));
        assertNull(r.jenkins.getItemByFullName("folder1/subfolder"));
        assertSame(r.jenkins.getItemByFullName("folder2/subfolder"), subFolder);
    }

    private void assertNews(String expected) {
        L l = r.jenkins.getExtensionList(ItemListener.class).get(L.class);
        assertEquals(expected, l.b.toString().trim());
        l.b.delete(0, l.b.length());
    }

    @TestExtension public static class L extends ItemListener {
        final StringBuilder b = new StringBuilder();
        @Override public void onCreated(Item item) {
            b.append(" created=").append(item.getFullName());
        }
        @Override public void onDeleted(Item item) {
            b.append(" deleted=").append(item.getFullName());
        }
        @Override public void onRenamed(Item item, String oldName, String newName) {
            assertEquals(item.getName(), newName);
            b.append(" renamed=").append(item.getFullName()).append(";from=").append(oldName);
        }
        @Override public void onLocationChanged(Item item, String oldFullName, String newFullName) {
            assertEquals(item.getFullName(), newFullName);
            b.append(" moved=").append(newFullName).append(";from=").append(oldFullName);
        }
    }

    private Folder createFolder() throws IOException {
        return r.jenkins.createProject(Folder.class, "folder" + (r.jenkins.getItems().size() + 1));
    }

    // todo move job to another folder
    // todo move folder to another folder

    public static class MyJsonObject {
        private String destination;

        //empty constructor required for JSON parsing.
        public MyJsonObject() {}

        public MyJsonObject(String message) {
            this.destination = message;
        }

        public void setMessage(String message) {
            this.destination = message;
        }

        public String getMessage() {
            return destination;
        }
    }
}
