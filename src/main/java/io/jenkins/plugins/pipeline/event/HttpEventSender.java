package io.jenkins.plugins.pipeline.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import io.jenkins.plugins.pipeline.event.json.EventJsonConfig;
import io.jenkins.plugins.pipeline.event.transformer.EventDataTransformers;
import net.sf.json.JSONObject;

/**
 * An implementation of EventSender, which sends event body via HTTP.
 * 
 * @author johnniang
 */
public class HttpEventSender implements EventSender {

    private final Logger logger = Logger.getLogger(HttpEventSender.class.getName());

    @Override
    public void send(Event event) {
        EventDataTransformers.INSTANCE.transform(event);

        // serialize event to JSON string
        String receiver = EventGlobalConfiguration.get().getReceiver();
        if (StringUtils.isBlank(receiver)) {
            logger.info("Skipped event sending due to receiver URL not set");
            return;
        }
        try (final CloseableHttpClient client = HttpClients.createDefault()) {
            String eventJSON = JSONObject.fromObject(event, new EventJsonConfig()).toString(4);
            HttpEntity httpEntity = EntityBuilder.create()
                    .setContentType(ContentType.APPLICATION_JSON)
                    .setText(eventJSON)
                    .build();
            HttpPost httpPost = new HttpPost(receiver);
            httpPost.setEntity(httpEntity);
            try (final CloseableHttpResponse response = client.execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();
                if (logger.isLoggable(Level.INFO)) {
                    logger.info("Event send successfully, and response: " + EntityUtils.toString(responseEntity));
                }
                EntityUtils.consumeQuietly(responseEntity);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to send event to " + receiver, e);
        }
    }

}
