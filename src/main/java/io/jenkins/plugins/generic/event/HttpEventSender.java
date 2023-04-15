package io.jenkins.plugins.generic.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.*;
import io.jenkins.plugins.generic.event.json.EventJsonConfig;
import io.jenkins.plugins.generic.event.transformer.EventDataTransformers;
import net.sf.json.JSONObject;
import org.apache.hc.core5.util.TimeValue;

/**
 * An implementation of EventSender, which sends event body via HTTP.
 * 
 * @author johnniang
 */
public class HttpEventSender implements EventSender {

    private final Logger logger = Logger.getLogger(HttpEventSender.class.getName());
    private final CustomCloseableHttpAsyncClient myClient = new CustomCloseableHttpAsyncClient();

    @Override
    public void send(Event event) {
        EventDataTransformers.INSTANCE.transform(event);

        final String receiver = EventGlobalConfiguration.get().getReceiver();

        if (StringUtils.isBlank(receiver)) {
            logger.info("Skipped event sending due to receiver URL not set");
            return;
        }

        try {
            myClient.sendPost(receiver, event, new FutureCallback<>() {
                @Override
                public void completed(SimpleHttpResponse response) {
                    logger.info("Event send succeeded, response: " + response.getBodyText() + ", data: " + event.toString());
                }

                @Override
                public void failed(Exception e) {
                    logger.info("Event send has been failed, response: " + e + ", data: " + event.toString());
                }

                @Override
                public void cancelled() {
                    logger.info("Event send has been cancelled");
                }
            });
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to send event to " + receiver, e);
        }

    }

    public static class CustomCloseableHttpAsyncClient {

        private static final CloseableHttpAsyncClient httpClient;
        private static String rootUrl = null;

        static {
            PoolingAsyncClientConnectionManager cmb = PoolingAsyncClientConnectionManagerBuilder
                    .create()
                    .setMaxConnPerRoute(1)
                    .setMaxConnTotal(1)
                    .setDefaultConnectionConfig(
                            ConnectionConfig
                                    .custom()
                                    .setTimeToLive(TimeValue.ofSeconds(10L))
                                    .build())
                    .build();

            httpClient = HttpAsyncClients.custom().setConnectionManager(cmb).build();
            httpClient.start();

            Jenkins j = Jenkins.getInstanceOrNull();
            if (j != null) {
                rootUrl = j.getRootUrl();
            }
        }
        public void sendPost(String receiver, Event event, final FutureCallback<SimpleHttpResponse> callback) {

            String eventJSON = JSONObject.fromObject(event, new EventJsonConfig()).toString(4);

            SimpleHttpRequest request = SimpleRequestBuilder
                    .post(receiver)
                    .setBody(eventJSON, ContentType.APPLICATION_JSON)
                    .addHeader("X-Event-Type", event.getType())
                    .addHeader("Referrer", rootUrl)
                    .build();

            httpClient.execute(request, callback);
        }
    }
}


