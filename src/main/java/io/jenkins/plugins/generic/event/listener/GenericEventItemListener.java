package io.jenkins.plugins.generic.event.listener;

import hudson.Extension;
import hudson.Util;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;
import io.jenkins.plugins.generic.event.Event;
import io.jenkins.plugins.generic.event.EventSender;
import io.jenkins.plugins.generic.event.HttpEventSender;
import io.jenkins.plugins.generic.event.MetaData;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

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

    public String getCanonicalItemUrl(Item item) {
        return item.getParent().getUrl() +
                item.getParent().getUrlChildPrefix() + '/' +
                item.getName() + '/';
    }

    public String getCanonicalItemOldUrl(Item item, String oldFullName) {
        String oldName = oldFullName.substring(oldFullName.lastIndexOf('/') + 1);
        return item.getParent().getUrl() +
                item.getParent().getUrlChildPrefix() + '/' +
                oldName + '/';
    }
    public String getCanonicalItemNewUrl(Item item, String newFullName) {
        String newName = newFullName.substring(newFullName.lastIndexOf('/') + 1);
        return item.getParent().getUrl() +
                item.getParent().getUrlChildPrefix() + '/' +
                newName + '/';
    }

    @Override
    public void onCreated(Item item) {
        eventSender.send(new Event.EventBuilder()
                .type("item.created")
                .source(item.getParent().getUrl())
                .url(this.getCanonicalItemUrl(item))
                .data(item)
                .build());
    }

    @Override
    public void onDeleted(Item item) {
        eventSender.send(new Event.EventBuilder()
                .type("item.deleted")
                .source(item.getParent().getUrl())
                .url(item.getUrl())
                .data(item)
                .build());
    }

    @Override
    public void onUpdated(Item item) {
        eventSender.send(new Event.EventBuilder()
                .type("item.updated")
                .source(item.getParent().getUrl())
                .url(item.getUrl())
                .data(item)
                .build());
    }

    @Override
    public void onLocationChanged(Item item, String oldFullName, String newFullName) {
        eventSender.send(new Event.EventBuilder()
                .type("item.locationChanged")
                .source(item.getParent().getUrl())
                .url(item.getUrl())
                .data(item)
                .metaData(new MetaData.MetaDataBuilder()
                        .oldName(oldFullName)
                        .newName(newFullName)
                        .oldUrl(this.getCanonicalItemOldUrl(item, oldFullName))
                        .newUrl(this.getCanonicalItemNewUrl(item, newFullName))
                        .build())
                .build());
    }
}
