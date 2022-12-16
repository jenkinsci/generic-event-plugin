package io.jenkins.plugins.generic.event.listener;

import com.cloudbees.hudson.plugins.folder.Folder;
import com.cloudbees.hudson.plugins.folder.relocate.DefaultRelocationUI;
import hudson.Extension;
import hudson.Util;
import hudson.model.*;
import hudson.model.listeners.ItemListener;
import io.jenkins.plugins.generic.event.Event;
import io.jenkins.plugins.generic.event.EventSender;
import io.jenkins.plugins.generic.event.HttpEventSender;
import io.jenkins.plugins.generic.event.MetaData;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;

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

    public String getCanonicalItemUrl(Item item) {
        return this.getCanonicalItemUrl(item, item.getName());
    }

    public String getCanonicalItemUrl(Item item, String fullName) {
        StringBuilder resultUrl = new StringBuilder();

        // todo rename folder event
        StaplerRequest req = Stapler.getCurrentRequest();
        List<Ancestor> ancs = Stapler.getCurrentRequest().getAncestors();
        for (Ancestor anc : ancs) {
            if (anc.equals(ancs.get(ancs.size()-1)) &&
                    Stapler.getCurrentRequest().getOriginalRequestURI().endsWith("confirmRename")) {
                continue;
            }

            Object o = anc.getObject();
            if (o instanceof Folder) {
                String urlToAdd = ((Folder) o).getName();
                resultUrl.append("job/");
                resultUrl.append(Util.rawEncode(urlToAdd));
                resultUrl.append("/");
            }
        }

//        if (item instanceof Project) {
//            resultUrl.append("job/");
//        }


        if (!(item instanceof Folder)) {
            String jobName = fullName.substring(fullName.lastIndexOf('/') + 1);
            resultUrl.append("job/");
            resultUrl.append(Util.rawEncode(jobName));
            resultUrl.append("/");
        } else {
            String folderName = fullName.substring(fullName.lastIndexOf('/') + 1);
            resultUrl.append("job/");
            resultUrl.append(Util.rawEncode(folderName));
        }

        return resultUrl.toString();
    }

    public String getCanonicalItemNewUrl(Item item, String newFullName) {

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
                .url(this.getCanonicalItemUrl(item))
                .data(item)
                .build());
    }

    @Override
    public void onUpdated(Item item) {
        eventSender.send(new Event.EventBuilder()
                .type("item.updated")
                .source(item.getParent().getUrl())
                .url(this.getCanonicalItemUrl(item))
                .data(item)
                .build());
    }

    @Override
    public void onLocationChanged(Item item, String oldFullName, String newFullName) {
        eventSender.send(new Event.EventBuilder()
                .type("item.locationChanged")
                .source(item.getParent().getUrl())
                .data(item)
                .metaData(new MetaData.MetaDataBuilder()
                        .oldName(oldFullName)
                        .newName(newFullName)
                        .oldUrl(this.getCanonicalItemUrl(item, oldFullName))
                        .newUrl(this.getCanonicalItemNewUrl(item, newFullName))
                        .build())
                .build());
    }
}
