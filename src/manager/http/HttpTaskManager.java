package manager.http;

import manager.FileBackedTaskManager;
import manager.ManagerSaveException;

public class HttpTaskManager extends FileBackedTaskManager {
    private KVTaskClient kvTaskClient;

    public HttpTaskManager(String url) {
        super();
        this.kvTaskClient = new KVTaskClient(url);
    }

    @Override
    protected void save() throws ManagerSaveException {
        for (Integer key : tasks.keySet())
            kvTaskClient.put(key.toString(), tasks.get(key).toString());

        for (Integer key : epics.keySet())
            kvTaskClient.put(key.toString(), epics.get(key).toString());

        for (Integer key : subTasks.keySet())
            kvTaskClient.put(key.toString(), subTasks.get(key).toString());
    }
}
