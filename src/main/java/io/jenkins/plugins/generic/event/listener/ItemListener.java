package io.jenkins.plugins.generic.event.listener;

import hudson.Extension;
import hudson.model.Item;
import io.jenkins.plugins.generic.event.Event;
import io.jenkins.plugins.generic.event.EventSender;
import io.jenkins.plugins.generic.event.HttpEventSender;

@Extension
public class ItemListener extends hudson.model.listeners.ItemListener {

    private EventSender eventSender;

    public ItemListener() {
        this.setEventSender(new HttpEventSender());
    }

    public void setEventSender(EventSender eventSender) {
        this.eventSender = eventSender;
    }

    @Override
    public void onCreated(Item item) {
        eventSender.send(new Event.EventBuilder()
                .type("item.created")
                .source(item.getParent().getUrl())
                .data(item)
                .build());
    }

    @Override
    public void onDeleted(Item item) {
        eventSender.send(new Event.EventBuilder()
                .type("item.deleted")
                .source(item.getParent().getUrl())
                .data(item)
                .build());
    }

    @Override
    public void onUpdated(Item item) {
        eventSender.send(new Event.EventBuilder()
                .type("item.updated")
                .source(item.getParent().getUrl())
                .data(item)
                .build());
    }
}
