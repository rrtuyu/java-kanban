package manager;

import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private static final byte HISTORY_CAPACITY = 10;
    private List<Task> history;

    public InMemoryHistoryManager(){
        history = new LinkedList<>();
    }

    @Override
    public void add(Task task){
        history.add(task);
        historyCheck();
    }

    @Override
    public List<Task> getHistory(){
        return history;
    }

    private void historyCheck() {
        if (history.size() > HISTORY_CAPACITY){
            history.remove(0);
        }
    }
}
