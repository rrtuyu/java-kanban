package test;

import kvserver.KVServer;
import manager.Managers;
import manager.http.HttpTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private static final String URL = "http://localhost:8078";
    private KVServer server;

    @Override
    @BeforeEach
    void taskSetInit() {
        try {
            server = new KVServer();
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.taskSetInit();
    }

    @AfterEach
    void stop() {
        server.stop();
    }

    @Override
    protected HttpTaskManager createNewManager() {
        return Managers.getHttpManager(URL);
    }

    @Test
    void loadFromServerTest() {
        HttpTaskManager testManager = HttpTaskManager.load(URL, URL);
        assertEquals(manager, testManager);
    }
}