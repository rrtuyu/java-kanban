package manager;

import task.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String DIR = "src/manager/history/";
    private String file;
    private static final String HEADER = "id,type,name,status,description,start,end,epic";

    protected FileBackedTaskManager() {
        super();
    }

    public FileBackedTaskManager(String file) {
        super();
        this.file = DIR + file;

        if (!Files.exists(Path.of(file))) {
            try {
                Files.createFile(Path.of(file));
            } catch (IOException e) {
                throw new ManagerSaveException(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        TaskManager tm = new FileBackedTaskManager("TM.csv");

        Task task1 = new Task("task1", "description1");
        task1.setDuration(LocalDateTime.of(2022, 12, 2, 0, 0), 60L);
        Task task2 = new Task("task2", "description2");
        task2.setDuration(LocalDateTime.of(2022, 12, 2, 1, 1), 15L);

        Epic epic1 = new Epic("epic1", "description1");
        Epic epic2 = new Epic("epic2", "description2");

        SubTask subTask1 = new SubTask("subtask1", "description1");
        subTask1.setDuration(LocalDateTime.of(2023, 4, 4, 12, 0), 15L);
        SubTask subTask2 = new SubTask("subtask2", "description2");
        subTask2.setDuration(subTask1.getEndTime().plusMinutes(1L), 20L);
        SubTask subTask3 = new SubTask("subtask3", "description3");

        tm.addEpic(epic1);
        tm.addSubTask(subTask1);
        tm.addTask(task1);
        tm.addEpic(epic2);
        tm.addSubTask(subTask2);
        tm.addTask(task2);
        tm.addSubTask(subTask3);

        tm.linkSubToEpic(subTask1, epic1);
        tm.linkSubToEpic(subTask2, epic1);
        tm.linkSubToEpic(subTask3, epic2);

        tm.getEpic(1);
        tm.getTask(3);
        tm.getEpic(1);
        tm.getSubTask(2);
        tm.getEpic(4);

        TaskManager loadedManager = loadFromFile(new File("src/manager/history/TM.csv"));
        System.out.println("initial managers' tasks\n" + tm.getTasks());
        System.out.println("loaded managers' tasks\n" + loadedManager.getTasks());

        System.out.println("\ninitial managers' epics\n" + tm.getEpics());
        System.out.println("loaded managers' epics\n" + loadedManager.getEpics());

        System.out.println("\ninitial managers' sub tasks\n" + tm.getSubTasks());
        System.out.println("loaded managers' sub tasks\n" + loadedManager.getSubTasks());

        System.out.println("\ninitial managers' history\n" + tm.getHistory());
        System.out.println("loaded managers' history\n" + loadedManager.getHistory());

        System.out.println("\nManager has been recovered properly: " + tm.equals(loadedManager));
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void linkSubToEpic(SubTask subTask, Epic epic) {
        super.linkSubToEpic(subTask, epic);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void updateTask(Task task, int id) {
        super.updateTask(task, id);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask, int id) {
        super.updateSubTask(subTask, id);
        save();
    }

    @Override
    public void updateEpic(Epic epic, int id) {
        super.updateEpic(epic, id);
        save();
    }

    protected void save() throws ManagerSaveException {
        try {
            writeDown(tasksAsStringList());
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }

    }

    private List<String> tasksAsStringList() {
        List<String> taskList = new ArrayList<>();

        for (Task task : tasks.values())
            taskList.add(toString(task));

        for (Epic epic : epics.values())
            taskList.add(toString(epic));

        for (SubTask subTask : subTasks.values())
            taskList.add(toString(subTask));

        return taskList;
    }

    private void writeDown(List<String> taskList) throws IOException {
        try (Writer writer = new FileWriter(file, false); BufferedWriter bw = new BufferedWriter(writer)) {
            bw.write(HEADER + "\n");

            for (String task : taskList)
                bw.write(task + "\n");

            bw.write("\n" + historyToString(history));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toString(Task task) {
        Type type = task.getType();
        String start = getDateTimeAsString(task.getStartTime());
        String end = getDateTimeAsString(task.getEndTime());

        String result = task.getId() + "," +
                type + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                start + "," +
                end;

        if (type == Type.SUBTASK) {
            SubTask sTask = (SubTask) task;
            Integer parent = sTask.getParentEpic();
            if (parent != null)
                result += "," + parent;
        }
        return result;
    }

    private String getDateTimeAsString(LocalDateTime dateTime) {
        if (dateTime == null || dateTime.isEqual(LocalDateTime.MAX))
            return "not set";

        return dateTime.format(Task.FORMATTER);
    }

    public static String historyToString(HistoryManager manager) {
        List<String> IdHistory = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            IdHistory.add(String.valueOf(task.getId()));
        }
        return String.join(",", IdHistory);
    }

    private Task fromString(String value) {
        String[] taskStr = value.split(",");

        Type type = Type.valueOf(taskStr[1]);
        Status status = Status.valueOf(taskStr[3]);
        String name = taskStr[2];
        String description = taskStr[4];
        int taskId = Integer.parseInt(taskStr[0]);

        String startStr = taskStr[5];
        String endStr = taskStr[6];
        LocalDateTime start = null;
        LocalDateTime end = null;
        Duration duration = null;
        if (!startStr.equals("not set") && !endStr.equals("not set")) {
            start = LocalDateTime.parse(startStr, Task.FORMATTER);
            end = LocalDateTime.parse(endStr, Task.FORMATTER);
            duration = Duration.between(start, end);
        }

        switch (type) {
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setStatus(status);
                epic.setId(taskId);
                addEpic(epic);
                return epic;
            case SUBTASK:
                SubTask subTask = new SubTask(name, description);
                subTask.setStatus(status);
                subTask.setId(taskId);
                addSubTask(subTask);
                if (start != null && duration != null)
                    subTask.setDuration(start, duration.toMinutes());
                if (taskStr.length == 8)
                    linkSubToEpic(subTask,
                            epics.get(Integer.parseInt(taskStr[7])));
                return subTask;
            default:
                Task task = new Task(name, description);
                task.setStatus(status);
                task.setId(taskId);
                if (start != null && duration != null)
                    task.setDuration(start, duration.toMinutes());
                addTask(task);
                return task;
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

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager("loadedTM.csv");
        try (Reader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
            List<String> content = getContent(br);
            for (String elem : content) {
                if (elem.isEmpty()) {
                    manager.loadHistory(historyFromString(content.get(content.size() - 1)));
                    break;
                }
                manager.fromString(elem);
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        return manager;
    }

    private static List<String> getContent(BufferedReader br) throws IOException {
        List<String> content = new ArrayList<>();
        br.readLine();
        while (br.ready()) {
            content.add(br.readLine());
        }
        return content;
    }
}
