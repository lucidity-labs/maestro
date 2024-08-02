package org.example.engine.internal.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.example.engine.internal.entity.EventModel;
import org.example.engine.internal.entity.WorkflowModel;
import org.example.engine.internal.repo.EventRepo;
import org.example.engine.internal.util.Json;

import java.io.IOException;
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

            server.setExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
