package io.jenkins.plugins.generic.event.listener;

import hudson.ExtensionList;
import io.jenkins.plugins.generic.event.Event;
import io.jenkins.plugins.generic.event.EventGlobalConfiguration;
import io.jenkins.plugins.generic.event.EventSender;
import jenkins.model.JenkinsLocationConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.kohsuke.stapler.Stapler;
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
    public void createFolder() throws IOException, Exception {
        doNothing().when(mockSender).send(any());

        MockFolder folderProject = r.createFolder("my-test-folder");

        r.waitUntilNoActivity();
        verify(mockSender, times(1)).send(any(Event.class));
    }
}
