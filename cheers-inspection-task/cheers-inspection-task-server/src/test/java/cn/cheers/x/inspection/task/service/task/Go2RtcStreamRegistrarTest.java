package cn.cheers.x.inspection.task.service.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Go2RtcStreamRegistrarTest {

    @Test
    void registerReceiveSlot_writesNameOnce() throws Exception {
        AtomicReference<String> config = new AtomicReference<>("streams:\n  robot-live:\n");
        AtomicReference<String> listed = new AtomicReference<>("{\"robot-live\":{}}");
        AtomicInteger posts = new AtomicInteger();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/streams", exchange -> respond(exchange, 200, listed.get()));
        server.createContext("/api/config", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                config.set(body);
                listed.set("{\"robot-live\":{},\"patrol-1-s1\":{}}");
                posts.incrementAndGet();
                respond(exchange, 200, "");
                return;
            }
            respond(exchange, 200, config.get());
        });
        server.start();
        try {
            String base = "http://127.0.0.1:" + server.getAddress().getPort();
            Go2RtcStreamRegistrar registrar = new Go2RtcStreamRegistrar(base, new ObjectMapper(), nullHttp());
            registrar.registerReceiveSlot("patrol-1-s1");
            registrar.registerReceiveSlot("patrol-1-s1");
            assertEquals(1, posts.get());
            assertTrue(config.get().contains("  patrol-1-s1:\n"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void registerReceiveSlot_reloadsWhenNameStaysOffTheList() throws Exception {
        AtomicReference<String> config = new AtomicReference<>("streams:\n  robot-live:\n");
        AtomicReference<String> listed = new AtomicReference<>("{\"robot-live\":{}}");
        AtomicInteger restarts = new AtomicInteger();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/streams", exchange -> respond(exchange, 200, listed.get()));
        server.createContext("/api/config", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                config.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
                respond(exchange, 200, "");
                return;
            }
            respond(exchange, 200, config.get());
        });
        server.createContext("/api/restart", exchange -> {
            restarts.incrementAndGet();
            listed.set("{\"robot-live\":{},\"patrol-1-s1\":{}}");
            respond(exchange, 200, "");
        });
        server.start();
        try {
            String base = "http://127.0.0.1:" + server.getAddress().getPort();
            Go2RtcStreamRegistrar registrar = new Go2RtcStreamRegistrar(base, new ObjectMapper(), nullHttp());
            registrar.registerReceiveSlot("patrol-1-s1");
            assertEquals(1, restarts.get());
            assertTrue(config.get().contains("  patrol-1-s1:\n"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void replaceTaskReceiveSlots_dropsUnusedNameAndKeepsAnotherTask() throws Exception {
        AtomicReference<String> config = new AtomicReference<>("""
                streams:
                  robot-live:
                  patrol-910104-patrol-task-910104-42:
                  patrol-task-910101-0:
                """);
        AtomicReference<String> listed = new AtomicReference<>(
                "{\"robot-live\":{},\"patrol-910104-patrol-task-910104-42\":{},\"patrol-task-910101-0\":{}}");
        AtomicInteger posts = new AtomicInteger();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/streams", exchange -> respond(exchange, 200, listed.get()));
        server.createContext("/api/config", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                config.set(body);
                listed.set("{\"robot-live\":{},\"patrol-910104-patrol-task-910104-42\":{},\"patrol-task-910101-1\":{}}");
                posts.incrementAndGet();
                respond(exchange, 200, "");
                return;
            }
            respond(exchange, 200, config.get());
        });
        server.start();
        try {
            String base = "http://127.0.0.1:" + server.getAddress().getPort();
            Go2RtcStreamRegistrar registrar = new Go2RtcStreamRegistrar(base, new ObjectMapper(), nullHttp());
            registrar.replaceTaskReceiveSlots(910101L, java.util.List.of("patrol-task-910101-1"));
            assertEquals(1, posts.get());
            assertTrue(config.get().contains("  patrol-task-910101-1:\n"));
            assertTrue(config.get().contains("  robot-live:\n"));
            assertTrue(config.get().contains("  patrol-910104-patrol-task-910104-42:\n"));
            assertFalse(config.get().contains("patrol-task-910101-0"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void registerReceiveSlot_rejectsWhenGatewayMissing() {
        Go2RtcStreamRegistrar registrar = new Go2RtcStreamRegistrar("", new ObjectMapper(), nullHttp());
        assertThrows(RuntimeException.class, () -> registrar.registerReceiveSlot("patrol-1-s1"));
    }

    private static java.net.http.HttpClient nullHttp() {
        return java.net.http.HttpClient.newHttpClient();
    }

    private static void respond(com.sun.net.httpserver.HttpExchange exchange, int status, String body) throws java.io.IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
