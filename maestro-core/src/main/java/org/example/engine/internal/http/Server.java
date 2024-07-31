package org.example.engine.internal.http;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Server {

    public static void serve() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/api/hello", (exchange -> {
                String response = "Hello World!";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }));
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
