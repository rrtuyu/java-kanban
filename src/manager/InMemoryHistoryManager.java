package manager;

import task.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final byte HISTORY_CAPACITY = 10;
    private List<Task> history;

    public InMemoryHistoryManager(){
        history = new ArrayList<>(HISTORY_CAPACITY);
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
            for (int i = 0; i < history.size(); i++)
                Collections.swap(history, i + 1, i);
        }
    }
}
