package com.felix;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class MainApp {

    public static final String BASE_URI = "http://localhost:8080/";

    public static Server startServer() {

        final ResourceConfig config = new ResourceConfig().packages("com.felix");

        final Server server =
                JettyHttpContainerFactory.createServer(URI.create(BASE_URI), config);

        return server;

    }

    public static void main(String[] args) {

        try {

            final Server server = startServer();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    System.out.println("shutting down");
                    server.stop();
                    System.out.println("exit");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));

            System.out.println(String.format("App is running %nTo stop, do CTRL+C"));

            Thread.currentThread().join();

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

}