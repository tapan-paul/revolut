package com.revolut;


import com.revolut.rest.controller.AccountResource;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ext.RuntimeDelegate;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class ApplicationServer {

    private HttpServer server;
    private InetSocketAddress address;
    private AtomicBoolean healthy;
    private static final short DEFAULT_PORT = 8500;

    private Logger logger = Logger.getLogger("http server");

    private ApplicationServer(Optional<String> listenAddr) throws IOException {

        Short port = Short.valueOf(listenAddr.orElseGet(
                () -> String.valueOf(DEFAULT_PORT)
        ));
        address = new InetSocketAddress(port);
        server = HttpServer.create(address, 0);
        healthy = new AtomicBoolean(true);

        server.createContext("/", RuntimeDelegate.getInstance().createEndpoint(makeConfig(), HttpHandler.class));
        server.createContext("/health", httpExchange -> {
            if (healthy.get())
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, -1);
            else
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_UNAVAILABLE, -1);
        });

        server.setExecutor(Executors.newCachedThreadPool());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("http server is shutting down...");
            shutDown();
            logger.info("http server stopped");
        }));
    }

    public static ApplicationServer getInstance(Optional<String> listenAddr) throws IOException {
        return new ApplicationServer(listenAddr);
    }

    public void shutDown() {
        healthy.set(false);
        server.stop(5);
    }

    public void start() {
        healthy.set(true);
        server.start();
    }

    private static ResourceConfig makeConfig () {
        ResourceConfig config = new ResourceConfig();
        config.register(AccountResource.class);
        return config;
    }

    public static void main(String[] args) throws Exception {

        Optional<String> listenAddr = Optional.empty();

        if (args.length > 0) {
            listenAddr = Optional.of(args[0]);
        }

        ApplicationServer server = ApplicationServer.getInstance(listenAddr);
        server.start();

    }

}
