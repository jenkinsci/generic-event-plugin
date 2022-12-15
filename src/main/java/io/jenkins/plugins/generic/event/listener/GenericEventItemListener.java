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
        StringBuffer resultUrl = new StringBuffer();

        List<Ancestor> ancs = Stapler.getCurrentRequest().getAncestors();
        for (Ancestor anc : ancs) {
            Object o = anc.getObject();
            if (o instanceof Hudson) {
                continue;
            }
            else if (o instanceof View) {
                continue;
            }
            else if (o instanceof Folder) {
                String urlToAdd = ((Folder) o).getName();
                resultUrl.append("job/");
                resultUrl.append(Util.rawEncode(urlToAdd));
            }
            else if (o instanceof Project) {
                continue;
            }
            else if (o instanceof Action) {
                continue;
            }
            else if (o instanceof DefaultRelocationUI) {
                continue;
            } else {
                String urlToAdd = ((View) o).getUrl();
                resultUrl.append(Util.rawEncode(urlToAdd));
            }

            resultUrl.append("/");
        }

        if (item instanceof Project) {
            resultUrl.append("job/");
        }

        String jobName = fullName.substring(fullName.lastIndexOf('/') + 1);
        return resultUrl + Util.rawEncode(jobName) + "/";
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
