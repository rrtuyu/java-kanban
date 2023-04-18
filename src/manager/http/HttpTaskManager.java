package manager.http;

import kvserver.KVServer;
import manager.FileBackedTaskManager;
import manager.ManagerSaveException;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import manager.Managers;
import manager.TaskManager;
import task.*;

public class HttpTaskManager extends FileBackedTaskManager {
    private KVTaskClient kvTaskClient;

    public HttpTaskManager(String url) {
        super();
        this.kvTaskClient = new KVTaskClient(url);
    }

    public static void main(String[] args) {
        try {
            new KVServer().start();
        } catch (IOException e) {
            System.out.println("Не получилось поднять сервер :(");
        }
        TaskManager manager = Managers.getDefault();

        Task t = new Task("test task", "test task");
        manager.addTask(t);

        Epic e = new Epic("test epic", "test epic");
        manager.addEpic(e);

        SubTask s = new SubTask("test sub", "test sub");
        manager.addSubTask(s);

        manager.linkSubToEpic(s, e);

        HttpTaskManager htm = HttpTaskManager.load("http://localhost:8078", "http://localhost:8078");
        System.out.println("initial managers' tasks\n" + manager.getTasks());
        System.out.println("loaded managers' tasks\n" + htm.getTasks());

        System.out.println("\ninitial managers' epics\n" + manager.getEpics());
        System.out.println("loaded managers' epics\n" + htm.getEpics());

        System.out.println("\ninitial managers' sub tasks\n" + manager.getSubTasks());
        System.out.println("loaded managers' sub tasks\n" + htm.getSubTasks());

        System.out.println("\ninitial managers' history\n" + manager.getHistory());
        System.out.println("loaded managers' history\n" + htm.getHistory());

        System.out.println(manager.equals(htm));
    }

    @Override
    protected void save() throws ManagerSaveException {
        for (Integer key : tasks.keySet())
            kvTaskClient.put(key.toString(), tasks.get(key).toString());

        for (Integer key : epics.keySet())
            kvTaskClient.put(key.toString(), epics.get(key).toString());

        for (Integer key : subTasks.keySet())
            kvTaskClient.put(key.toString(), subTasks.get(key).toString());

        if (!history.getHistory().isEmpty())
            kvTaskClient.put("history", historyToString(history));
    }

    public static HttpTaskManager load(String srcUrl, String targetUrl) {
        Integer id = 1;
        HttpTaskManager newManager = new HttpTaskManager(targetUrl);
        KVTaskClient client = new KVTaskClient(srcUrl);
        while (true) {
            try {
                String value = client.load(id.toString());
                newManager.fromString(value);
            } catch (KVClientException e) {
                break;
            }
            id++;
        }
        try {
            newManager.loadHistory(historyFromString(client.load("history")));
        } catch (KVClientException e) {
            System.out.println("Failed too load history");
        }
        return newManager;
    }

    private Task fromString(String value) {
        int contentStartIndex = value.indexOf("{");
        String content = value.substring(contentStartIndex);
        Type type = Type.valueOf(value.replace(content, "").toUpperCase());

        String[] split = content.replaceAll("(id=| status=| name=| description=| start=| end=|[{}'])", "")
                .split(",");
        int id = Integer.parseInt(split[0]);
        Status status = Status.valueOf(split[1].toUpperCase());
        String name = split[2];
        String description = split[3];

        String startStr = split[4];
        String endStr = split[5];
        LocalDateTime start = null;
        Duration duration = null;
        LocalDateTime end = null;
        if (!startStr.equals("not set") && !endStr.equals("not set")) {
            start = LocalDateTime.parse(startStr, Task.FORMATTER);
            end = LocalDateTime.parse(endStr, Task.FORMATTER);
            duration = Duration.between(start, end);
        }

        switch (type) {
            case TASK:
                Task task = new Task(name, description);
                task.setStatus(status);
                task.setId(id);
                if (start != null && duration != null)
                    task.setDuration(start, duration.toMinutes());
                addTask(task);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setStatus(status);
                epic.setId(id);
                addEpic(epic);
                return epic;
            case SUBTASK:
                SubTask subTask = new SubTask(name, description);
                subTask.setStatus(status);
                subTask.setId(id);
                if (start != null && duration != null)
                    subTask.setDuration(start, duration.toMinutes());
                String parentStr = split[6].replace(" parentEpicId=", "");
                addSubTask(subTask);
                if (!parentStr.equals("null"))
                    linkSubToEpic(subTask, epics.get(Integer.parseInt(parentStr)));
                return subTask;
            default:
                throw new KVClientException("Server response is incorrect:\n" + value);
        }
    }

    private static List<Integer> historyFromString(String strHistory) {
        List<Integer> h = new ArrayList<>();
        if (!strHistory.isEmpty()) {
            for (String id : strHistory.split(",")) {
                h.add(Integer.parseInt(id));
            }
        }
        return h;
    }

    private void loadHistory(List<Integer> history) {
        for (int item : history) {
            if (tasks.containsKey(item)) {
                this.history.add(tasks.get(item));
            } else if (epics.containsKey(item)) {
                this.history.add((epics.get(item)));
            } else if (subTasks.containsKey(item)) {
                this.history.add(subTasks.get(item));
            }
        }
    }
}
