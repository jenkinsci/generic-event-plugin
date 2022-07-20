package io.jenkins.plugins.generic.event;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
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
    CustomCloseableHttpAsyncClient myClient = new CustomCloseableHttpAsyncClient();

    final String receiver = EventGlobalConfiguration.get().getReceiver();

    @Override
    public void send(Event event) {
        EventDataTransformers.INSTANCE.transform(event);

        try {
            new Thread(() -> myClient.sendPost(receiver, event, new FutureCallback<SimpleHttpResponse>() {
                @Override
                public void completed(SimpleHttpResponse response) {
                    logger.info("Event send succeeded, response: " + response.getBodyText());
                }

                @Override
                public void failed(Exception e) {
                    logger.info("Event send has been failed, response: " + e);
                }

                @Override
                public void cancelled() {
                    logger.info("Event send has been cancelled");
                }
            })).start();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to send event to " + receiver, e);
        }

    }

    static public class CustomCloseableHttpAsyncClient {
        private final CloseableHttpAsyncClient httpClient;

        {
            PoolingAsyncClientConnectionManager cmb = PoolingAsyncClientConnectionManagerBuilder
                    .create()
                    .setMaxConnPerRoute(1)
                    .setMaxConnTotal(1)
                    .setConnectionTimeToLive(TimeValue.ofSeconds(10L))
                    .build();

            httpClient = HttpAsyncClients.custom().setConnectionManager(cmb).build();
            httpClient.start();
        }

        public void sendPost(String receiver, Event event, final FutureCallback<SimpleHttpResponse> callback) {

            String eventJSON = JSONObject.fromObject(event, new EventJsonConfig()).toString(4);

            SimpleHttpRequest request = SimpleRequestBuilder
                    .post(receiver)
                    .setBody(eventJSON, ContentType.APPLICATION_JSON)
                    .build();

            httpClient.execute(request, callback);
        }
    }
}


