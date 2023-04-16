package test;

import kvserver.KVServer;
import manager.Managers;
import manager.http.HttpTaskManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private static final KVServer server;

    static {
        try {
            server = new KVServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void start() {
        server.start();
    }

    @AfterAll
    static void stop() {
        server.stop();
    }

    @Override
    protected HttpTaskManager createNewManager() {
        return Managers.getHttpManager("http://localhost:8078");
    }
}