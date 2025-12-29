package io.jenkins.plugins.generic.event.listener;

import hudson.ExtensionList;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import hudson.model.Items;
import hudson.model.listeners.ItemListener;
import io.jenkins.plugins.generic.event.Event;
import io.jenkins.plugins.generic.event.EventGlobalConfiguration;
import io.jenkins.plugins.generic.event.EventSender;
import jenkins.model.JenkinsLocationConfiguration;
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

    @Before
    public void setUp() {
        EventGlobalConfiguration config = EventGlobalConfiguration.get();
        config.setReceiver("http://localhost:8000");
        JenkinsLocationConfiguration.get().setUrl(null);

        MockitoAnnotations.openMocks(this);
        ExtensionList<GenericEventItemListener> listeners = ExtensionList.lookup(GenericEventItemListener.class);
        listeners.forEach((listener) -> listener.setEventSender(mockSender));
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

    @Test public void createDeleteWithDisabledEvents() throws Exception {
        // Disable created and deleted events
        EventGlobalConfiguration config = EventGlobalConfiguration.get();
        config.setSendItemCreated(false);
        config.setSendItemDeleted(false);

        MockFolder folder = r.createFolder("test-folder");
        FreeStyleProject project = folder.createProject(FreeStyleProject.class, "test-job");

        // No created events should be sent
        verify(mockSender, never()).send(any(Event.class));

        project.delete();
        folder.delete();

        // No deleted events should be sent
        verify(mockSender, never()).send(any(Event.class));

        // Re-enable for other tests
        config.setSendItemCreated(true);
        config.setSendItemDeleted(true);
    }

    @Test public void renameWithDisabledLocationChangedEvents() throws Exception {
        // Disable location changed events
        EventGlobalConfiguration config = EventGlobalConfiguration.get();
        config.setSendItemLocationChanged(false);

        MockFolder folder = r.createFolder("folder");
        FreeStyleProject project = folder.createProject(FreeStyleProject.class, "old-name");
        reset(mockSender);

        project.renameTo("new-name");

        // No location changed events should be sent
        verify(mockSender, never()).send(any(Event.class));

        // Re-enable for other tests
        config.setSendItemLocationChanged(true);
    }

    @Test public void createWithMatchingPattern() throws Exception {
        // Set pattern to match jobs in "folder"
        EventGlobalConfiguration config = EventGlobalConfiguration.get();
        config.setJobNamePatterns("folder\nfolder/.*");

        MockFolder folder = r.createFolder("folder");
        FreeStyleProject project = folder.createProject(FreeStyleProject.class, "test-job");

        // Both folder and folder/test-job should send events
        verify(mockSender, times(2)).send(any(Event.class));

        // Clear pattern for other tests
        config.setJobNamePatterns(null);
    }

    @Test public void createWithNonMatchingPattern() throws Exception {
        // Set pattern that won't match
        EventGlobalConfiguration config = EventGlobalConfiguration.get();
        config.setJobNamePatterns("production/.*");

        MockFolder folder = r.createFolder("folder");
        FreeStyleProject project = folder.createProject(FreeStyleProject.class, "test-job");

        // No events should be sent
        verify(mockSender, never()).send(any(Event.class));

        // Clear pattern for other tests
        config.setJobNamePatterns(null);
    }

    @Test public void renameWithPatternMatchingNewName() throws Exception {
        MockFolder folder = r.createFolder("folder");
        FreeStyleProject project = folder.createProject(FreeStyleProject.class, "old-name");
        reset(mockSender);

        // Set pattern to match the new name
        EventGlobalConfiguration config = EventGlobalConfiguration.get();
        config.setJobNamePatterns("folder/new-name");

        project.renameTo("new-name");

        // Location changed event should be sent because new name matches
        verify(mockSender, times(1)).send(any(Event.class));

        // Clear pattern for other tests
        config.setJobNamePatterns(null);
    }

    @Test public void deleteFreeStyleJobAndFolder() throws Exception {

        MockFolder folder = r.createFolder("folder");
        FreeStyleProject project = folder.createProject(FreeStyleProject.class, "freestyle-job");
        assertNews("created=folder created=folder/freestyle-job");
        assertSame(r.jenkins.getItemByFullName("folder/freestyle-job"), project);
        reset(mockSender);

        project.delete();
        assertNews("deleted=folder/freestyle-job");
        verify(mockSender).send(any(Event.class));
        assertNull(r.jenkins.getItemByFullName("folder/freestyle-job"));
        reset(mockSender);

        folder.delete();
        assertNews("deleted=folder");
        verify(mockSender, times(1)).send(any(Event.class));
        assertNull(r.jenkins.getItemByFullName("folder"));
    }

    private void assertNews(String expected) {
        ItemListenerLogger extensionList = r.jenkins.getExtensionList(ItemListener.class).get(ItemListenerLogger.class);
        assertEquals(expected, extensionList.b.toString().trim());
        extensionList.b.delete(0, extensionList.b.length());
    }

    @TestExtension public static class ItemListenerLogger extends ItemListener {
        final StringBuilder b = new StringBuilder();
        @Override public void onCreated(Item item) {
            b.append(" created=").append(item.getFullName());
        }

        @Override public void onUpdated(Item item) {
            b.append(" updated=").append(item.getFullName());
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
