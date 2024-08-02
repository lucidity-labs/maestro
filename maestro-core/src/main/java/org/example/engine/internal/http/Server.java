package org.example.engine.internal.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.example.engine.internal.entity.EventModel;
import org.example.engine.internal.entity.WorkflowModel;
import org.example.engine.internal.repo.EventRepo;
import org.example.engine.internal.util.Json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

public class Server {

    public static void serve() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

            server.createContext("/api/workflows", exchange -> {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");

                if ("GET".equals(exchange.getRequestMethod())) handleGetWorkflows(exchange);
                else sendMethodNotAllowedResponse(exchange);
            });

            server.createContext("/", exchange -> {
                String requestPath = exchange.getRequestURI().getPath();
                String filePath;

                if (requestPath.equals("/")) {
                    filePath = "/nextjs-app/index.html";
                } else if (requestPath.startsWith("/_next/")) {
                    // Handle Next.js generated files
                    filePath = "/nextjs-app" + requestPath;
                } else {
                    // Handle other static files or app routes
                    filePath = "/nextjs-app/" + requestPath;
                }

                try (InputStream is = Server.class.getResourceAsStream(filePath)) {
                    if (is != null) {
                        byte[] bytes = is.readAllBytes();
                        String contentType = getContentType(filePath);
                        exchange.getResponseHeaders().set("Content-Type", contentType);
                        exchange.sendResponseHeaders(200, bytes.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(bytes);
                        }
                    } else {
                        String response = "404 (Not Found)";
                        exchange.sendResponseHeaders(404, response.length());
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, 0);
                }
            });

            server.setExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getContentType(String filePath) {
        if (filePath.endsWith(".html")) return "text/html";
        if (filePath.endsWith(".js")) return "application/javascript";
        if (filePath.endsWith(".css")) return "text/css";
        if (filePath.endsWith(".json")) return "application/json";
        if (filePath.endsWith(".png")) return "image/png";
        if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) return "image/jpeg";
        if (filePath.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }

    private static void handleGetWorkflows(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        if (pathParts.length == 3) handleGetAllWorkflows(exchange);
        else if (pathParts.length == 4) handleGetWorkflowById(exchange, pathParts[3]);
        else sendInvalidPathResponse(exchange);
    }

    private static void handleGetAllWorkflows(HttpExchange exchange) throws IOException {
        List<WorkflowModel> workflowModels = EventRepo.getWorkflows();
        String json = Json.serialize(workflowModels);
        sendJsonResponse(exchange, 200, json);
    }

    private static void handleGetWorkflowById(HttpExchange exchange, String id) throws IOException {
        List<EventModel> eventModels = EventRepo.get(id);
        String json = Json.serialize(eventModels);
        sendJsonResponse(exchange, 200, json);
    }

    private static void sendInvalidPathResponse(HttpExchange exchange) throws IOException {
        sendJsonResponse(exchange, 400, "{\"error\": \"Invalid path\"}");
    }

    private static void sendMethodNotAllowedResponse(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, -1);
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
