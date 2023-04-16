package manager;

import manager.http.HttpTaskManager;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return getHttpManager("http://localhost:8078");
    }

    public static TaskManager getDefaultFbManager() {
        return new FileBackedTaskManager("defaultManager.csv");
    }

    public static TaskManager getFileBackedManager(String fileName) {
        return FileBackedTaskManager.loadFromFile(new File(fileName));
    }

    public static TaskManager getInMemoryManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static HttpTaskManager getHttpManager(String url) {
        return new HttpTaskManager(url);
    }
}
