package com.example;

import io.nats.client.*;
import io.nats.client.api.RetentionPolicy;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

// https://nats.io/blog/jetstream-java-client-04-push-subscribe/
/*
 * subject.test.1, subject.test.2   --> jetstream --> async
 * subject.test.3                   --> nats      --> sync
 * */
public class App {

    private static final String SERVER_URL = "nats://localhost:4222";
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException, JetStreamApiException, TimeoutException {
        String arg = args[0];

        switch (arg) {
            case "jetstream" -> jetStreamFlow();
            case "nats" -> natsFlow();
            default -> showError();
        }
    }

    private static void showError() {
        logger.log(Level.INFO, "Los argumentos válidos son jetstream o nats");
    }

    private static void natsFlow() throws IOException, InterruptedException {
        logger.log(Level.INFO, "Nats Flow");
        syncSubscribeToSubject();
    }

    private static void syncSubscribeToSubject() throws IOException, InterruptedException {
        Options options = new Options.Builder().server(SERVER_URL).build();
        Connection nc = Nats.connect(options);

        String subject = "subject.test.3";
        String queueName = "queue-test";

        Dispatcher dispatcher = nc.createDispatcher(message -> {
            byte[] dataBytes = message.getData();
            String replyTo = message.getReplyTo();

            logger.log(Level.INFO, "[Message arrived]: {0}", new String(dataBytes));
            nc.publish(replyTo, "Message from subscriber (it will be json)".getBytes());
        });

        dispatcher.subscribe(subject, queueName);

        logger.log(Level.INFO, "Subscribe to {0} queue: {1}", new Object[] { subject, queueName });

    }

    private static void jetStreamFlow() throws IOException, InterruptedException, JetStreamApiException, TimeoutException {
        logger.info("JetStream Flow");
        checkStreamIsCreated();
        asyncSubscribeToSubject();
    }

    private static void asyncSubscribeToSubject() throws IOException, InterruptedException, JetStreamApiException, TimeoutException {
        Options options = new Options.Builder().server(SERVER_URL).build();
        Connection nc = Nats.connect(options);
        JetStream js = nc.jetStream();

        String streamName = "stream-test";
        String subject = "subject.test.1";
        String queueName = "queue-test"; // 3 instancias, llegue a las 3, como balanceador
        String durableName = "durable"; // enviar los mensajes que quedaron
        boolean autoAck = true; // ack, mensaje, le avisas al servidor nats, que le mensaje llego

        PushSubscribeOptions push = PushSubscribeOptions.builder()
                .stream(streamName)
                .durable(durableName)
                .build();

        MessageHandler handler = (Message message) -> {
            byte[] data = message.getData();
            logger.log(Level.INFO, "Mensaje recibido: {0}", new String(data));
        };

        Dispatcher dispatcher = nc.createDispatcher();

        js.subscribe(subject, queueName, dispatcher, handler, autoAck, push);

        nc.flush(Duration.ofSeconds(1)); // i dont know
    }

    private static void checkStreamIsCreated() throws IOException, InterruptedException, JetStreamApiException {
        Options options = new Options.Builder().server(SERVER_URL).build();
        Connection nc = Nats.connect(options);

        String streamName = "stream-test";
        String subjectName1 = "subject.test.1";
        String subjectName2 = "subject.test.2";
        try {
            nc.jetStreamManagement().getStreamInfo(streamName);

            logger.log(Level.INFO, "Stream {0} already exists", streamName);

            nc.close();
        } catch (JetStreamApiException e) {
            logger.info("stream not found");
            createTopicAndSubjects(nc, streamName, subjectName1, subjectName2);
        }
    }

    private static void createTopicAndSubjects(Connection nc, String streamName, String subjectName1, String subjectName2)
            throws IOException, JetStreamApiException, InterruptedException {
        StreamConfiguration sc = StreamConfiguration.builder()
                .name(streamName)
                .subjects(subjectName1, subjectName2)
                .storageType(StorageType.File)
                .retentionPolicy(RetentionPolicy.WorkQueue)
                .build();

        nc.jetStreamManagement().addStream(sc);

        logger.log(Level.INFO, "Stream creado: {0}", streamName);

        nc.close();
    }
}
