package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.Managers;
import manager.TaskManager;
import manager.http.DurationAdapter;
import manager.http.HttpTaskServer;
import manager.http.LocalDateTimeAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


class HttpTaskServerTest {
    private HttpTaskServer server;
    private final HttpClient client = HttpClient.newHttpClient();
    private final HttpResponse.BodyHandler<String> rHandler = HttpResponse.BodyHandlers.ofString();
    private TaskManager manager;

    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @BeforeEach
    void start() throws IOException {
        manager = Managers.getFileBackedManager("src/manager/history/TM.csv");
        server = new HttpTaskServer();
        server.start();
    }

    @AfterEach
    void stop() {
        server.stop();
    }

    @Test
    void commonEndpointTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/test");
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(404, resp.statusCode());
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        String expected = gson.toJson(manager.getTasks());
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(expected, resp.body());
    }

    @Test
    void getEpics() throws IOException, InterruptedException {
        String expected = gson.toJson(manager.getEpics());
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(expected, resp.body());
    }

    @Test
    void getSubtasks() throws IOException, InterruptedException {
        String expected = gson.toJson(manager.getSubTasks());
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(expected, resp.body());
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        String expected = gson.toJson(manager.getTask(6));
        URI uri = URI.create("http://localhost:8080/tasks/task?id=6");
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(expected, resp.body());
    }

    @Test
    void getTaskNullTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/task?id=0");
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(404, resp.statusCode());
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        String expected = gson.toJson(manager.getEpic(1));
        URI uri = URI.create("http://localhost:8080/tasks/epic?id=1");
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(expected, resp.body());
    }

    @Test
    void getEpicNullTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/epic?id=0");
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(404, resp.statusCode());
    }

    @Test
    void getSubById() throws IOException, InterruptedException {
        String expected = gson.toJson(manager.getSubTask(2));
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=2");
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(expected, resp.body());
    }

    @Test
    void getSubNullTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=0");
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(404, resp.statusCode());
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        String expected = gson.toJson(manager.getHistory());
        URI uri = URI.create("http://localhost:8080/tasks/history");
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(expected, resp.body());
    }

    @Test
    void getPriority() throws IOException, InterruptedException {
        String expected = gson.toJson(manager.getPrioritizedTasks());
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(expected, resp.body());
    }

    @Test
    void getSubsOfEpic() throws IOException, InterruptedException {
        Epic epic = manager.getEpic(1);
        String expected = gson.toJson(manager.getSubTasksOf(epic));
        URI uri = URI.create("http://localhost:8080/tasks/subtask/epic/?id=1");
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(expected, resp.body());
    }

    @Test
    void getSubsOfUnlistedEpic() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/subtask/epic/?id=0");
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(404, resp.statusCode());
    }

    @Test
    void postTask() throws IOException, InterruptedException {
        Task task = new Task("test", "test");
        manager.addTask(task);
        String expected = gson.toJson(manager.getTasks());

        String json = "{\"name\":\"test\",\"description\":\"test\",\"status\"=\"NEW\"}";
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(json);
        URI uriPost = URI.create("http://localhost:8080/tasks/task");
        HttpRequest req = HttpRequest.newBuilder()
                .POST(publisher)
                .uri(uriPost)
                .build();
        HttpResponse resp1 = client.send(req, rHandler);

        URI uriGet = URI.create("http://localhost:8080/tasks/task");
        HttpRequest req2 = HttpRequest.newBuilder()
                .GET()
                .uri(uriGet)
                .build();
        HttpResponse resp2 = client.send(req2, rHandler);

        assertEquals(201, resp1.statusCode());
        assertEquals(expected, resp2.body());
    }

    @Test
    void postEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("test", "test");
        manager.addEpic(epic);
        String json = "{\"name\":\"test\",\"description\":\"test\",\"status\"=\"NEW\"}";
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(json);
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest req = HttpRequest.newBuilder()
                .POST(publisher)
                .uri(uri)
                .build();
        HttpResponse resp1 = client.send(req, rHandler);

        assertEquals(201, resp1.statusCode());
    }

    @Test
    void postSub() throws IOException, InterruptedException {
        SubTask sub = new SubTask("test", "test");
        manager.addSubTask(sub);
        String json = "{\"name\":\"test\",\"description\":\"test\",\"status\"=\"NEW\"}";
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(json);
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest req = HttpRequest.newBuilder()
                .POST(publisher)
                .uri(uri)
                .build();
        HttpResponse resp1 = client.send(req, rHandler);

        assertEquals(201, resp1.statusCode());
    }

    @Test
    void deleteTasks() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest req = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(202, resp.statusCode());
    }

    @Test
    void deleteEpics() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest req = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(202, resp.statusCode());
    }

    @Test
    void deleteSubs() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest req = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(202, resp.statusCode());
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/task?id=3");
        HttpRequest req = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(202, resp.statusCode());
    }

    @Test
    void deleteUnlistedTask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/task?id=10");
        HttpRequest req = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(404, resp.statusCode());
    }

    @Test
    void deleteEpic() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/epic?id=1");
        HttpRequest req = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(202, resp.statusCode());
    }

    @Test
    void deleteUnlistedEpic() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/epic?id=10");
        HttpRequest req = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(404, resp.statusCode());
    }

    @Test
    void deleteSub() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=2");
        HttpRequest req = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(202, resp.statusCode());
    }

    @Test
    void deleteUnlistedSub() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=10");
        HttpRequest req = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse resp = client.send(req, rHandler);
        assertEquals(404, resp.statusCode());
    }
}