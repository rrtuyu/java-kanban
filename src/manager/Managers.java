package manager;

public class Managers {

    public static TaskManager getDefault() {
        return new FileBackedTaskManager("TM.csv");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
