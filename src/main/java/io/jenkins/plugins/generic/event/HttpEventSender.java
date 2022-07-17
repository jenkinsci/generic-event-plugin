package io.jenkins.plugins.generic.event;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.impl.bootstrap.HttpAsyncRequester;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import io.jenkins.plugins.generic.event.json.EventJsonConfig;
import io.jenkins.plugins.generic.event.transformer.EventDataTransformers;
import net.sf.json.JSONObject;
import org.apache.hc.core5.http.nio.AsyncClientEndpoint;
import org.apache.hc.core5.http.nio.entity.StringAsyncEntityConsumer;
import org.apache.hc.core5.http.nio.support.AsyncRequestBuilder;
import org.apache.hc.core5.http.nio.support.BasicResponseConsumer;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.http2.frame.RawFrame;
import org.apache.hc.core5.http2.impl.nio.H2StreamListener;
import org.apache.hc.core5.http2.impl.nio.bootstrap.H2RequesterBootstrap;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.Timeout;

/**
 * An implementation of EventSender, which sends event body via HTTP.
 * 
 * @author johnniang
 */
public class HttpEventSender implements EventSender {

    private final Logger logger = Logger.getLogger(HttpEventSender.class.getName());

    final IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
            .setSoTimeout(5, TimeUnit.SECONDS)
            .build();

    final H2Config h2Config = H2Config.custom()
            .setPushEnabled(false)
            .setMaxConcurrentStreams(100)
            .build();

    final HttpAsyncRequester requester = H2RequesterBootstrap.bootstrap()
            .setIOReactorConfig(ioReactorConfig)
            .setH2Config(h2Config)
            .setStreamListener(new H2StreamListener() {
                @Override
                public void onHeaderInput(HttpConnection httpConnection, int i, List<? extends Header> list) {

                }

                @Override
                public void onHeaderOutput(HttpConnection httpConnection, int i, List<? extends Header> list) {

                }

                @Override
                public void onFrameInput(HttpConnection httpConnection, int i, RawFrame rawFrame) {

                }

                @Override
                public void onFrameOutput(HttpConnection httpConnection, int i, RawFrame rawFrame) {

                }

                @Override
                public void onInputFlowControl(HttpConnection httpConnection, int i, int i1, int i2) {

                }

                @Override
                public void onOutputFlowControl(HttpConnection httpConnection, int i, int i1, int i2) {

                }
            })
            .create();

    final String receiver = EventGlobalConfiguration.get().getReceiver();
    final String uri = "http://localhost:8000/event-get";

    public void send(Event event) {
        EventDataTransformers.INSTANCE.transform(event);

        try {
            requester.start();

            final HttpHost target = HttpHost.create(URI.create(receiver));
            final Future<AsyncClientEndpoint> future = requester.connect(target, Timeout.ofSeconds(60));
            final AsyncClientEndpoint clientEndpoint = future.get();
            final CountDownLatch latch = new CountDownLatch(1);

            clientEndpoint.execute(
                    AsyncRequestBuilder.get()
                            .setUri(uri)
                            .build(),
                    new BasicResponseConsumer<>(new StringAsyncEntityConsumer()),
                    new FutureCallback<Message<HttpResponse, String>>() {
                        @Override
                        public void completed(Message<HttpResponse, String> message) {
                            latch.countDown();
                            final HttpResponse response = message.getHead();
                            final String body = message.getBody();

                            System.out.println("/event-get response code: " + response.getCode());
                            System.out.println(body);
                        }

                        @Override
                        public void failed(Exception e) {
                            latch.countDown();
                            System.out.println(uri + "->" + e);
                        }

                        @Override
                        public void cancelled() {
                            latch.countDown();
                            System.out.println(uri + " cancelled");
                        }
                    }
            );

            latch.await();
//            clientEndpoint.releaseAndDiscard();  // Нужно завершать только при завершении процесса Jenkins
//            System.out.println("Shutting down I/O reactor");
//            requester.initiateShutdown();

        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to send event to " + receiver, e);
        }





    }

//    @Override
    public void sendSync(Event event) {
        EventDataTransformers.INSTANCE.transform(event);

        // serialize event to JSON string
        String receiver = EventGlobalConfiguration.get().getReceiver();
        if (StringUtils.isBlank(receiver)) {
            logger.info("Skipped event sending due to receiver URL not set");
            return;
        }
        // TODO Make HTTP request asynchronous
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
