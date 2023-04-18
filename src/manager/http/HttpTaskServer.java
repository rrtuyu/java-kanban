package manager.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static TaskManager manager;
    private HttpServer server;

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }

    public HttpTaskServer() throws IOException {
        manager = Managers.getFileBackedManager("src/manager/history/TM.csv");
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler());
    }


    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    static class TaskHandler implements HttpHandler {

        private static Gson gson;

        public TaskHandler() {
            gson = new GsonBuilder()
                    .serializeNulls()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            Endpoint endpoint = getEndpoint(path, query, exchange.getRequestMethod());
            switch (endpoint) {

                case GET_TASKS:
                    handleGetTasksByType(exchange, "task");
                    break;
                case GET_EPICS:
                    handleGetTasksByType(exchange, "epic");
                    break;
                case GET_SUBTASKS:
                    handleGetTasksByType(exchange, "subtask");
                    break;
                case GET_HISTORY:
                    handleGetHistory(exchange);
                    break;
                case GET_PRIORITY:
                    handleGetPriority(exchange);
                    break;
                case GET_TASK_ID:
                    try {
                        int id = Integer.parseInt(query.replace("id=", ""));
                        handleGetTaskByID(exchange, "task", id);
                    } catch (NumberFormatException e) {
                        writeResponse(exchange, "id cannot be literal", 400);
                        break;
                    }
                    break;
                case GET_EPIC_ID:
                    try {
                        int id = Integer.parseInt(query.replace("id=", ""));
                        handleGetTaskByID(exchange, "epic", id);
                    } catch (NumberFormatException e) {
                        writeResponse(exchange, "id cannot be literal", 400);
                        break;
                    }
                    break;
                case GET_SUBTASK_ID:
                    try {
                        int id = Integer.parseInt(query.replace("id=", ""));
                        handleGetTaskByID(exchange, "subtask", id);
                    } catch (NumberFormatException e) {
                        writeResponse(exchange, "id cannot be literal", 400);
                        break;
                    }
                    break;
                case GET_SUBTASKS_OF_EPIC:
                    try {
                        int id = Integer.parseInt(query.replace("id=", ""));
                        handleGetEpicSubtasks(exchange, id);
                    } catch (NumberFormatException e) {
                        writeResponse(exchange, "id cannot be literal", 400);
                        break;
                    }
                    break;
                case POST_TASK:
                    handlePostTaskByType(exchange, "task");
                    break;
                case POST_EPIC:
                    handlePostTaskByType(exchange, "epic");
                    break;
                case POST_SUBTASK:
                    handlePostTaskByType(exchange, "subtask");
                    break;
                case DELETE_TASKS:
                    handleDeleteTasksByType(exchange, "task");
                    break;
                case DELETE_EPICS:
                    handleDeleteTasksByType(exchange, "epic");
                    break;
                case DELETE_SUBTASKS:
                    handleDeleteTasksByType(exchange, "subtask");
                    break;
                case DELETE_TASK:
                    try {
                        int id = Integer.parseInt(query.replace("id=", ""));
                        handleDeleteTaskById(exchange, "task", id);
                    } catch (NumberFormatException e) {
                        writeResponse(exchange, "id cannot be literal", 400);
                        break;
                    }
                    break;
                case DELETE_EPIC:
                    try {
                        int id = Integer.parseInt(query.replace("id=", ""));
                        handleDeleteTaskById(exchange, "epic", id);
                    } catch (NumberFormatException e) {
                        writeResponse(exchange, "id cannot be literal", 400);
                        break;
                    }
                    break;
                case DELETE_SUBTASK:
                    try {
                        int id = Integer.parseInt(query.replace("id=", ""));
                        handleDeleteTaskById(exchange, "subtask", id);
                    } catch (NumberFormatException e) {
                        writeResponse(exchange, "id cannot be literal", 400);
                        break;
                    }
                    break;
                case UNKNOWN:
                    writeResponse(exchange, "Incorrect request", 400);
                    break;
            }

        }

        private Endpoint getEndpoint(final String path, final String query, final String method) { //метод жака фреско
            String[] split = path.split("/");
            switch (method) {
                case "GET":
                    if (query == null) {
                        if (split.length == 2 && split[1].equals("tasks"))
                            return Endpoint.GET_PRIORITY;

                        if (split.length == 3) {
                            if (split[2].equals("task"))
                                return Endpoint.GET_TASKS;
                            if (split[2].equals("epic"))
                                return Endpoint.GET_EPICS;
                            if (split[2].equals("subtask"))
                                return Endpoint.GET_SUBTASKS;
                            if (split[2].equals("history"))
                                return Endpoint.GET_HISTORY;
                        }

                    } else {
                        if (split.length == 3) {
                            if (split[2].equals("task") && query.startsWith("id="))
                                return Endpoint.GET_TASK_ID;
                            if (split[2].equals("epic") && query.startsWith("id="))
                                return Endpoint.GET_EPIC_ID;
                            if (split[2].equals("subtask") && query.startsWith("id="))
                                return Endpoint.GET_SUBTASK_ID;
                        }

                        if (split.length == 4) {
                            if (split[2].equals("subtask") && split[3].equals("epic") && query.startsWith("id="))
                                return Endpoint.GET_SUBTASKS_OF_EPIC;
                        }
                    }
                    return Endpoint.UNKNOWN;

                case "POST":
                    if (split.length == 3 && query == null) {
                        if (split[2].equals("task"))
                            return Endpoint.POST_TASK;
                        if (split[2].equals("epic"))
                            return Endpoint.POST_EPIC;
                        if (split[2].equals("subtask"))
                            return Endpoint.POST_SUBTASK;
                    }
                    return Endpoint.UNKNOWN;

                case "DELETE":
                    if (query == null) {
                        if (split.length == 3 && split[2].equals("task"))
                            return Endpoint.DELETE_TASKS;
                        if (split.length == 3 && split[2].equals("epic"))
                            return Endpoint.DELETE_EPICS;
                        if (split.length == 3 && split[2].equals("subtask"))
                            return Endpoint.DELETE_SUBTASKS;
                    } else {
                        if (split.length == 3 && split[2].equals("task") && query.startsWith("id="))
                            return Endpoint.DELETE_TASK;
                        if (split.length == 3 && split[2].equals("epic") && query.startsWith("id="))
                            return Endpoint.DELETE_EPIC;
                        if (split.length == 3 && split[2].equals("subtask") && query.startsWith("id="))
                            return Endpoint.DELETE_SUBTASK;
                    }
                    return Endpoint.UNKNOWN;

                default:
                    return Endpoint.UNKNOWN;
            }
        }

        //GET MAPPING
        private void handleGetPriority(HttpExchange exchange) throws IOException {
            String resp = gson.toJson(manager.getPrioritizedTasks());
            writeResponse(exchange, resp, 200);
        }

        private void handleGetTasksByType(HttpExchange exchange, String type) throws IOException {
            switch (type) {
                case "task":
                    writeResponse(exchange, gson.toJson(manager.getTasks()), 200);
                    break;
                case "epic":
                    writeResponse(exchange, gson.toJson(manager.getEpics()), 200);
                    break;
                case "subtask":
                    writeResponse(exchange, gson.toJson(manager.getSubTasks()), 200);
                    break;
            }
        }

        private void handleGetHistory(HttpExchange exchange) throws IOException {
            String resp = gson.toJson(manager.getHistory());
            writeResponse(exchange, resp, 200);
        }

        private void handleGetTaskByID(HttpExchange exchange, String type, int id) throws IOException {
            String resp;
            switch (type) {
                case "task":
                    try {
                        resp = gson.toJson(manager.getTask(id));
                        writeResponse(exchange, resp, 200);
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, e.getMessage(), 404);
                    }
                    break;
                case "epic":
                    try {
                        resp = gson.toJson(manager.getEpic(id));
                        writeResponse(exchange, resp, 200);
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, e.getMessage(), 404);
                    }
                    break;
                case "subtask":
                    try {
                        resp = gson.toJson(manager.getSubTask(id));
                        writeResponse(exchange, resp, 200);
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, e.getMessage(), 404);
                    }
                    break;
            }
        }

        private void handleGetEpicSubtasks(HttpExchange exchange, int id) throws IOException {
            try {
                Epic epic = manager.getEpic(id);
                String resp = gson.toJson(manager.getSubTasksOf(epic));
                writeResponse(exchange, resp, 200);
            } catch (IllegalArgumentException e) {
                writeResponse(exchange, e.getMessage(), 404);
            }
        }

        //POST MAPPING
        private void handlePostTaskByType(HttpExchange exchange, String type) throws IOException {
            byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
            String taskBody = new String(bodyBytes);
            switch (type) {
                case "task":
                    try {
                        Task task = gson.fromJson(taskBody, Task.class);
                        manager.addTask(task);
                        writeResponse(exchange, "task successfully added", 201);
                    } catch (JsonSyntaxException | IllegalArgumentException e) {
                        writeResponse(exchange, e.getMessage(), 400);
                    }
                    break;
                case "epic":
                    try {
                        Epic epic = gson.fromJson(taskBody, Epic.class);
                        manager.addEpic(epic);
                        writeResponse(exchange, "nice", 201);
                    } catch (JsonSyntaxException | IllegalArgumentException e) {
                        writeResponse(exchange, e.getMessage(), 400);
                    }
                    break;
                case "subtask":
                    try {
                        SubTask subTask = gson.fromJson(taskBody, SubTask.class);
                        manager.addSubTask(subTask);
                        writeResponse(exchange, "nice", 201);
                    } catch (JsonSyntaxException | IllegalArgumentException e) {
                        writeResponse(exchange, e.getMessage(), 400);
                    }
                    break;
            }
        }

        //DELETE MAPPING
        private void handleDeleteTasksByType(HttpExchange exchange, String type) throws IOException {
            switch (type) {
                case "task":
                    manager.clearTasks();
                    writeResponse(exchange, "all tasks removed", 202);
                    break;
                case "epic":
                    manager.clearEpics();
                    writeResponse(exchange, "all epics removed", 202);
                    break;
                case "subtask":
                    manager.clearSubTasks();
                    writeResponse(exchange, "all sub tasks removed", 202);
                    break;
            }
        }

        private void handleDeleteTaskById(HttpExchange exchange, String type, int id) throws IOException {
            switch (type) {
                case "task":
                    try {
                        manager.removeTask(id);
                        writeResponse(exchange, "task successfully removed", 202);
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, e.getMessage(), 404);
                    }
                    break;
                case "epic":
                    try {
                        manager.removeEpic(id);
                        writeResponse(exchange, "epic successfully removed", 202);
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, e.getMessage(), 404);
                    }
                    break;
                case "subtask":
                    try {
                        manager.removeSubTask(id);
                        writeResponse(exchange, "sub task successfully removed", 202);
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, e.getMessage(), 404);
                    }
                    break;
            }
        }

        private void writeResponse(HttpExchange exchange,
                                   String responseMessage,
                                   int responseCode) throws IOException {

            try (OutputStream os = exchange.getResponseBody()) {
                exchange.sendResponseHeaders(responseCode, 0);
                os.write(responseMessage.getBytes());
            } catch (IOException e) {
                exchange.sendResponseHeaders(500, 0);
            }
        }

        private enum Endpoint {
            GET_PRIORITY, GET_HISTORY,
            GET_TASKS, GET_EPICS, GET_SUBTASKS,
            GET_TASK_ID, GET_EPIC_ID, GET_SUBTASK_ID,
            GET_SUBTASKS_OF_EPIC,
            POST_TASK, POST_EPIC, POST_SUBTASK,
            DELETE_TASKS, DELETE_EPICS, DELETE_SUBTASKS,
            DELETE_TASK, DELETE_EPIC, DELETE_SUBTASK,
            UNKNOWN
        }
    }
}
