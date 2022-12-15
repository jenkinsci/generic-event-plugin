package io.jenkins.plugins.generic.event.listener;

import com.cloudbees.hudson.plugins.folder.Folder;
import com.cloudbees.hudson.plugins.folder.relocate.DefaultRelocationUI;
import com.cloudbees.hudson.plugins.folder.relocate.RelocationAction;
import hudson.Extension;
import hudson.Util;
import hudson.model.*;
import hudson.model.listeners.ItemListener;
import hudson.security.AccessControlled;
import hudson.util.Iterators;
import io.jenkins.plugins.generic.event.Event;
import io.jenkins.plugins.generic.event.EventSender;
import io.jenkins.plugins.generic.event.HttpEventSender;
import io.jenkins.plugins.generic.event.MetaData;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.Stapler;
import hudson.Functions;
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
        StringBuffer resultUrl = new StringBuffer();

        List<Ancestor> ancs = Stapler.getCurrentRequest().getAncestors();
        for (Ancestor anc : ancs) {
            Object o = anc.getObject();
            String tmpUrl = anc.getUrl();
            if (o instanceof Hudson) {
                continue;
            }
            else if (o instanceof View) {
//                String urlToRemove = ((View) o).getUrl();
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
//        String oldResultUrl = item.getParent().getUrl() + item.getParent().getUrlChildPrefix() + '/' + jobName + '/';
        String oldResultUrl = resultUrl + jobName + "/";

        return Util.rawEncode(oldResultUrl);
    }

    public String getCanonicalItemNewUrl(Item item, String oldFullName, String newFullName) {
        StringBuffer resultUrl = new StringBuffer();
        List<Ancestor> ancs = Stapler.getCurrentRequest().getAncestors();
        List<String> tokens = new ArrayList<>();


        for (Ancestor anc : ancs) {
            Object o = anc.getObject();
            String tmpUrl = anc.getUrl();
            if (o instanceof Hudson) {
                continue;
            }
            else if (o instanceof View) {
//                String urlToRemove = ((View) o).getUrl();
                continue;
            }
            else if (o instanceof Folder) {
                String urlToAdd = ((Folder) o).getName();
                tokens.add("job/");
                resultUrl.append("job/");
//                resultUrl.append(Util.rawEncode(urlToAdd));
                resultUrl.append(urlToAdd);
                tokens.add(urlToAdd);
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
//                resultUrl.append(Util.rawEncode(urlToAdd));
                resultUrl.append(urlToAdd);
                tokens.add(urlToAdd);
            }

            resultUrl.append("/");
            tokens.add("/");
        }

        if (item instanceof Project) {
            resultUrl.append("job/");
            tokens.add("job/");
        }

//        String jobName = Util.rawEncode(newFullName.substring(newFullName.lastIndexOf('/') + 1));
        String oldFolder = oldFullName.split("/")[0];
        String newFolder = newFullName.split("/")[0];
        String jobName = newFullName.substring(newFullName.lastIndexOf('/') + 1);
//        String oldResultUrl = item.getParent().getUrl() + item.getParent().getUrlChildPrefix() + '/' + jobName + '/';
        String oldResultUrl = item.getParent().getUrl() + item.getParent().getUrlChildPrefix() + '/' + jobName + '/';
        String newJobUrl = resultUrl + jobName + "/";

        String oldResultUrlEncoded = item.getParent().getUrl() + item.getParent().getUrlChildPrefix() + '/' + Util.rawEncode(jobName) + '/';
        return oldResultUrlEncoded;
//        return Util.rawEncode(newJobUrl);
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
                        .newUrl(this.getCanonicalItemNewUrl(item, oldFullName, newFullName))
                        .build())
                .build());
    }
}
