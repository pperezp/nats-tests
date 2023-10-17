package com.example;

import io.nats.client.*;

import java.io.IOException;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* Cosas ya hechas:
*   - "Balanceador" de mensajes si existen muchas instancias de un microservicio
*   - Si se envia un mensaje asincrono y no hay suscriptores, una vez que
*       se conecte algun suscriptor le llegan los mensajes.
    - Enviar un mensaje sincrono y recibir la respuesta correspondiente.
* */

public class App {

    private static final String SERVER_URL = "nats://localhost:4222";
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException, JetStreamApiException {
        String arg = args[0];

        switch (arg) {
            case "jetstream" -> jetStreamFlow();
            case "nats" -> natsFlow();
            default -> showError();
        }
    }

    private static void natsFlow() throws IOException, InterruptedException {
        logger.log(Level.INFO, "Nats flow --> Sync Message");
        publishSyncMessage();
    }

    private static void jetStreamFlow() throws JetStreamApiException, IOException, InterruptedException {
        logger.log(Level.INFO, "JetStream flow --> Async Message");
        publishAsyncMessage();
    }

    private static void showError() {
        logger.log(Level.INFO, "Los argumentos válidos son jetstream o nats");
    }

    private static void publishAsyncMessage() throws IOException, InterruptedException, JetStreamApiException {
        Options options = new Options.Builder().server(SERVER_URL).build();
        Connection nc = Nats.connect(options);

        String subject = "subject.test.1";

        JetStream jetStream = nc.jetStream();

        for (int i = 1; i <= 15; i++) {
            String message = "[" + i + "] ASYNC message from client";
            jetStream.publish(subject, message.getBytes());
        }

        logger.log(Level.INFO, "Mensajes publicados");
        nc.close();
    }

    private static void publishSyncMessage() throws IOException, InterruptedException {
        Options options = new Options.Builder().server(SERVER_URL).build();
        Connection nc = Nats.connect(options);

        String subject = "subject.test.3";
        byte[] message = "SYNC message".getBytes();
        int timeOutInSeconds = 5;

        Message msg = nc.request(subject, message, Duration.ofSeconds(timeOutInSeconds));

        if (msg == null) {
            logger.log(Level.INFO, "Message is null (timeout 5 seconds)");
            nc.close();
            return;
        }

        byte[] data = msg.getData();
        logger.log(Level.INFO, "Response: {0}", new String(data));

        nc.close();
    }
}
