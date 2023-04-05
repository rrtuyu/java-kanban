package manager;

public class Managers {

    public static TaskManager getDefault() {
        return new FileBackedTaskManager("TM.csv");
    }

    public static TaskManager getFileBackedManager(String fileName) {
        return new FileBackedTaskManager(fileName);
    }

    public static TaskManager getInMemoryManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
