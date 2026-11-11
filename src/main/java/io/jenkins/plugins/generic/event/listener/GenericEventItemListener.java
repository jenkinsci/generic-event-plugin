package io.jenkins.plugins.generic.event.listener;

import com.cloudbees.hudson.plugins.folder.Folder;
import hudson.Extension;
import hudson.Util;
import hudson.model.*;
import hudson.model.listeners.ItemListener;
import io.jenkins.plugins.generic.event.Event;
import io.jenkins.plugins.generic.event.EventGlobalConfiguration;
import io.jenkins.plugins.generic.event.EventSender;
import io.jenkins.plugins.generic.event.HttpEventSender;
import io.jenkins.plugins.generic.event.MetaData;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.Stapler;

import java.util.ArrayList;
import java.util.List;

/**
 * This listener collects events about all items.
 *
 * @author johnniang
 */
@Extension
public class GenericEventItemListener extends ItemListener {

    private EventSender eventSender;

    public GenericEventItemListener() {
        this.setEventSender(new HttpEventSender());
    }

    public void setEventSender(EventSender eventSender) {
        this.eventSender = eventSender;
    }

    /**
     * Normalizes url for canonical representation
     *
     * <p>
     * For example, if the url was "view/myView/job/folder/job/taskName/",
     * then result will be "job/folder/job/taskName/"
     *
     * @param fullName Project name
     */
    public String getCanonicalEventUrl(String fullName, String source) {

        StringBuilder resultUrl = new StringBuilder();

        if (Stapler.getCurrentRequest2() == null) {
            return "";
        }

        String entityName = fullName.substring(fullName.lastIndexOf('/') + 1);

        if (!source.isEmpty()) {
            resultUrl.append(source);
            System.out.println(source);
        }

        resultUrl.append("job/");
        resultUrl.append(Util.rawEncode(entityName));
        resultUrl.append("/");

        return resultUrl.toString();
    }

    public String getCanonicalEventUrlNewLocation(Item item, String newFullName) {

        String jobName = newFullName.substring(newFullName.lastIndexOf('/') + 1);
        List<String> urls_list = new ArrayList<>();
        AbstractItem _item = (AbstractItem) item;
        while (_item.getParent() != null) {
            if (_item.getParent() instanceof Hudson) {
                break;
            }
            String _url = ((AbstractItem) _item.getParent()).getShortUrl();
            urls_list.add(0, _url);
            _item = (AbstractItem) _item.getParent();
        }

        return String.join("", urls_list) + item.getParent().getUrlChildPrefix() + '/' + Util.rawEncode(jobName) + '/';
    }

    @Override
    public void onCreated(Item item) {
        EventGlobalConfiguration config = EventGlobalConfiguration.get();
        if (config != null && config.isSendItemCreated() && config.matchesJobNamePattern(item.getFullName())) {
            eventSender.send(new Event.EventBuilder()
                    .type("item.created")
                    .source(item.getParent().getUrl())
                    .url(this.getCanonicalEventUrl(item.getName(), item.getParent().getUrl()))
                    .data(item)
                    .build());
        }
    }

    @Override
    public void onDeleted(Item item) {
        EventGlobalConfiguration config = EventGlobalConfiguration.get();
        if (config != null && config.isSendItemDeleted() && config.matchesJobNamePattern(item.getFullName())) {
            eventSender.send(new Event.EventBuilder()
                    .type("item.deleted")
                    .source(item.getParent().getUrl())
                    .url(this.getCanonicalEventUrl(item.getName(), item.getParent().getUrl()))
                    .data(item)
                    .build());
        }
    }

    @Override
    public void onUpdated(Item item) {
        EventGlobalConfiguration config = EventGlobalConfiguration.get();
        if (config != null && config.isSendItemUpdated() && config.matchesJobNamePattern(item.getFullName())) {
            eventSender.send(new Event.EventBuilder()
                    .type("item.updated")
                    .source(item.getParent().getUrl())
                    .url(this.getCanonicalEventUrl(item.getName(), item.getParent().getUrl()))
                    .data(item)
                    .build());
        }
    }

    @Override
    public void onLocationChanged(Item item, String oldFullName, String newFullName) {
        EventGlobalConfiguration config = EventGlobalConfiguration.get();
        if (config != null && config.isSendItemLocationChanged() && config.matchesJobNamePattern(newFullName)) {
            eventSender.send(new Event.EventBuilder()
                    .type("item.locationChanged")
                    .source(item.getParent().getUrl())
                    .data(item)
                    .metaData(new MetaData.MetaDataBuilder()
                            .oldName(oldFullName)
                            .newName(newFullName)
                            .oldUrl(Stapler.getCurrentRequest2() == null ? "" :
                                    java.util.Arrays.stream(oldFullName.split("/"))
                                            .map(name -> "job/" + Util.rawEncode(name) + "/")
                                            .collect(java.util.stream.Collectors.joining()))
                            .newUrl(this.getCanonicalEventUrlNewLocation(item, newFullName))
                            .build())
                    .build());
        }
    }
}
