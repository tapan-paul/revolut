package com.revolut;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.logging.Logger;

public class Main {

    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws Exception{

        Optional<InetSocketAddress> listenAddr = Optional.empty();

        if (args.length>0) {
            listenAddr = Optional.of(new InetSocketAddress(args[0].split(":")[0], Integer.parseInt(args[0].split(":")[1])));
        }

        ApplicationServer server = ApplicationServer.getInstance(listenAddr);
        server.start();
    }
}
