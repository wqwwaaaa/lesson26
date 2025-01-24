package zad3;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

class PostsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Получите информацию об эндпоинте, к которому был запрос
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_POSTS: {
                writeResponse(exchange, "Получен запрос на получение постов", 200);
                break;
            }
            case GET_COMMENTS: {
                writeResponse(exchange, "Получен запрос на получение комментариев", 200);
                break;
            }
            case POST_COMMENT: {
                writeResponse(exchange, "Получен запрос на добавление комментария", 200);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        // Разделяем путь на части
        String[] parts = requestPath.split("/");

        if (requestMethod.equals("GET") && parts.length == 3 && parts[1].equals("posts")) {
            return Endpoint.GET_POSTS;
        } else if (requestMethod.equals("GET") && parts.length == 4 && parts[1].equals("posts")
                && parts[3].equals("comments")) {
            return Endpoint.GET_COMMENTS;
        } else if (requestMethod.equals("POST") && parts.length == 4 && parts[1].equals("posts")
                && parts[3].equals("comments")) {
            return Endpoint.POST_COMMENT;
        }
        return Endpoint.UNKNOWN;
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        exchange.sendResponseHeaders(responseCode, responseString.isEmpty() ? 0 : responseString.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            if (!responseString.isEmpty()) {
                os.write(responseString.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    enum Endpoint {
        GET_POSTS, GET_COMMENTS, POST_COMMENT, UNKNOWN
    }
}

public class Practicum {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        // Конфигурация и запуск сервера
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/posts", new PostsHandler());
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        // Завершаем работу сервера для корректной работы тренажёра
        // httpServer.stop(1);
    }
}