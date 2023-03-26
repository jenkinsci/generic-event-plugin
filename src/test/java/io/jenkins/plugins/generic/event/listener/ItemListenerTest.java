package io.jenkins.plugins.generic.event.listener;

import hudson.ExtensionList;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import hudson.model.Items;
import hudson.model.listeners.ItemListener;
import io.jenkins.plugins.generic.event.Event;
import io.jenkins.plugins.generic.event.EventGlobalConfiguration;
import io.jenkins.plugins.generic.event.EventSender;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockFolder;
import org.jvnet.hudson.test.TestExtension;
import org.mockito.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemListenerTest {

    @Rule public JenkinsRule r = new JenkinsRule();

    @Mock
    EventSender mockSender;

    ItemListenerLogger extensionList;

    @Before
    public void setUp() {
        EventGlobalConfiguration config = EventGlobalConfiguration.get();
        config.setReceiver("http://localhost:5000/base");
        MockitoAnnotations.openMocks(this);
        ExtensionList<GenericEventItemListener> listeners = ExtensionList.lookup(GenericEventItemListener.class);
        listeners.forEach((listener) -> listener.setEventSender(mockSender));
        extensionList = r.jenkins.getExtensionList(ItemListener.class).get(ItemListenerLogger.class);
    }

    @Test public void renameFreeStyleJob() throws Exception {
        MockFolder folder = r.createFolder("folder");
        FreeStyleProject project = folder.createProject(FreeStyleProject.class, "old-job-name");
        assertNews("created=folder created=folder/old-job-name");
        assertSame(r.jenkins.getItemByFullName("folder/old-job-name"), project);
        reset(mockSender);

        project.renameTo("new-job-name");
        assertNews("renamed=folder/new-job-name;from=old-job-name moved=folder/new-job-name;from=folder/old-job-name");
        verify(mockSender, times(1)).send(any(Event.class));
        assertNull(r.jenkins.getItemByFullName("folder/old-job-name"));
        assertSame(r.jenkins.getItemByFullName("folder/new-job-name"), project);
    }

    @Test public void renameFolder() throws Exception {
        MockFolder folder = r.createFolder("folder");
        MockFolder subFolder = folder.createProject(MockFolder.class, "old-subfolder");
        assertNews("created=folder created=folder/old-subfolder");
        assertSame(r.jenkins.getItemByFullName("folder/old-subfolder"), subFolder);
        reset(mockSender);

        subFolder.renameTo("new-subfolder");
        assertNews("renamed=folder/new-subfolder;from=old-subfolder moved=folder/new-subfolder;from=folder/old-subfolder");
        verify(mockSender, times(1)).send(any(Event.class));
        assertNull(r.jenkins.getItemByFullName("folder/old-subfolder"));
        assertSame(r.jenkins.getItemByFullName("folder/new-subfolder"), subFolder);
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
        assertEquals(expected, extensionList.b.toString().trim());
        extensionList.b.delete(0, extensionList.b.length());
    }

    @TestExtension public static class ItemListenerLogger extends ItemListener {
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
}
