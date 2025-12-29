package io.jenkins.plugins.generic.event;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest2;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Extension(ordinal = 100)
@Symbol("eventDispatcher")
public class EventGlobalConfiguration extends GlobalConfiguration {

    private String receiver;
    private String jobNamePatterns;

    // RunListener events
    private boolean sendRunStarted = true;
    private boolean sendRunCompleted = true;
    private boolean sendRunFinalized = true;
    private boolean sendRunInitialized = true;
    private boolean sendRunDeleted = true;

    // ItemListener events
    private boolean sendItemCreated = true;
    private boolean sendItemDeleted = true;
    private boolean sendItemUpdated = true;
    private boolean sendItemLocationChanged = true;

    public EventGlobalConfiguration() {
        this.load();
    }

    public static EventGlobalConfiguration get() {
        return GlobalConfiguration.all().get(EventGlobalConfiguration.class);
    }

    @Override
    public boolean configure(StaplerRequest2 req, JSONObject json) {
        req.bindJSON(this, json);
        this.save();
        return true;
    }

    @Override
    public String getDisplayName() {
        return "Event Dispatcher";
    }

    public String getReceiver() {
        return this.receiver;
    }

    @DataBoundSetter
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getJobNamePatterns() {
        return this.jobNamePatterns;
    }

    @DataBoundSetter
    public void setJobNamePatterns(String jobNamePatterns) {
        this.jobNamePatterns = jobNamePatterns;
    }

    /**
     * Checks if a job name matches any of the configured regex patterns.
     * If no patterns are configured, returns true (allow all).
     *
     * @param jobName The full name of the job/item to check
     * @return true if the job name matches at least one pattern or no patterns are configured
     */
    public boolean matchesJobNamePattern(String jobName) {
        if (jobNamePatterns == null || jobNamePatterns.trim().isEmpty()) {
            return true; // No filter configured, allow all
        }

        String[] patterns = jobNamePatterns.split("\\r?\\n");
        for (String patternStr : patterns) {
            patternStr = patternStr.trim();
            if (patternStr.isEmpty()) {
                continue;
            }

            try {
                Pattern pattern = Pattern.compile(patternStr);
                if (pattern.matcher(jobName).matches()) {
                    return true;
                }
            } catch (PatternSyntaxException e) {
                // Invalid pattern, skip it
                continue;
            }
        }

        return false; // No pattern matched
    }

    // RunListener event getters and setters
    public boolean isSendRunStarted() {
        return this.sendRunStarted;
    }

    @DataBoundSetter
    public void setSendRunStarted(boolean sendRunStarted) {
        this.sendRunStarted = sendRunStarted;
    }

    public boolean isSendRunCompleted() {
        return this.sendRunCompleted;
    }

    @DataBoundSetter
    public void setSendRunCompleted(boolean sendRunCompleted) {
        this.sendRunCompleted = sendRunCompleted;
    }

    public boolean isSendRunFinalized() {
        return this.sendRunFinalized;
    }

    @DataBoundSetter
    public void setSendRunFinalized(boolean sendRunFinalized) {
        this.sendRunFinalized = sendRunFinalized;
    }

    public boolean isSendRunInitialized() {
        return this.sendRunInitialized;
    }

    @DataBoundSetter
    public void setSendRunInitialized(boolean sendRunInitialized) {
        this.sendRunInitialized = sendRunInitialized;
    }

    public boolean isSendRunDeleted() {
        return this.sendRunDeleted;
    }

    @DataBoundSetter
    public void setSendRunDeleted(boolean sendRunDeleted) {
        this.sendRunDeleted = sendRunDeleted;
    }

    // ItemListener event getters and setters
    public boolean isSendItemCreated() {
        return this.sendItemCreated;
    }

    @DataBoundSetter
    public void setSendItemCreated(boolean sendItemCreated) {
        this.sendItemCreated = sendItemCreated;
    }

    public boolean isSendItemDeleted() {
        return this.sendItemDeleted;
    }

    @DataBoundSetter
    public void setSendItemDeleted(boolean sendItemDeleted) {
        this.sendItemDeleted = sendItemDeleted;
    }

    public boolean isSendItemUpdated() {
        return this.sendItemUpdated;
    }

    @DataBoundSetter
    public void setSendItemUpdated(boolean sendItemUpdated) {
        this.sendItemUpdated = sendItemUpdated;
    }

    public boolean isSendItemLocationChanged() {
        return this.sendItemLocationChanged;
    }

    @DataBoundSetter
    public void setSendItemLocationChanged(boolean sendItemLocationChanged) {
        this.sendItemLocationChanged = sendItemLocationChanged;
    }
}
