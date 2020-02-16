package com.revolut;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class ApplicationServer  {

    private HttpServer server;
    private InetSocketAddress address;
    private AtomicBoolean healthy;
    private static final short DEFAULT_PORT = 8500;

    private Logger logger = Logger.getLogger("http server");

    private ApplicationServer(Optional<InetSocketAddress> listenAddr) throws IOException {

        address = listenAddr.orElse(new InetSocketAddress(DEFAULT_PORT));
        server = HttpServer.create(address, 0);
        healthy = new AtomicBoolean(true);

        server.createContext("/", httpExchange -> {

        });

        server.setExecutor(Executors.newCachedThreadPool());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("http server is shutting down...");
            healthy.set(false);
            shutDown();
            logger.info("http server stopped");
        }));

    }

    public static ApplicationServer getInstance(Optional<InetSocketAddress> listenAddr) throws IOException {
        return new ApplicationServer(listenAddr);
    }

    public void shutDown() {
        server.stop(5);
    }

    public void start() {
        healthy.set(true);
        server.start();
    }


}
