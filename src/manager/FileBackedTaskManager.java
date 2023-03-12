package manager;

import task.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String DIR = "src/manager/history/";
    private String file;
    private static final String HEADER = "id,type,name,status,description,epic";
    private final List<String> taskList;

    private FileBackedTaskManager() {
        super();
        taskList = new ArrayList<>();
        this.file = DIR + "DefaultTM.csv";

        if (!Files.exists(Path.of(file))) {
            try {
                Files.createFile(Path.of(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public FileBackedTaskManager(String file) {
        super();
        taskList = new ArrayList<>();
        this.file = DIR + file;

        if (!Files.exists(Path.of(file))) {
            try {
                Files.createFile(Path.of(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        FileBackedTaskManager tm = new FileBackedTaskManager("TM.csv");

        Task task1 = new Epic("name1", "description1");
        Task task2 = new Epic("name2", "description2");
        Epic epic1 = new Epic("name1", "description1");
        Epic epic2 = new Epic("name2", "description2");
        SubTask subTask1 = new SubTask("name1", "description1");
        SubTask subTask2 = new SubTask("name2", "description2");
        SubTask subTask3 = new SubTask("name3", "description3");

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

        FileBackedTaskManager loadedManager = loadFromFile(new File("src/manager/history/TM.csv"));
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        updateTaskList();
        save();
    }

    private void addTask(Task task, int id) {
        tasks.put(id, task);
        task.setId(id);
        updateTaskList();
        this.id++;
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        updateTaskList();
        save();
    }

    private void addEpic(Epic epic, int id) {
        epics.put(id, epic);
        epic.setId(id);
        updateTaskList();
        this.id++;
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        updateTaskList();
        save();
    }

    private void addSubTask(SubTask subTask, int id) {
        subTasks.put(id, subTask);
        subTask.setId(id);
        updateTaskList();
        this.id++;
    }

    @Override
    public void linkSubToEpic(SubTask subTask, Epic epic) {
        super.linkSubToEpic(subTask, epic);
        updateTaskList();
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

    private void save() throws ManagerSaveException {
        try {
            writeDown();
        } catch (IOException e) {
            throw new ManagerSaveException("Unable to write history in " + file);
        }

    }

    private void updateTaskList() { //выделил метод для сбора тасков из контейнеров, чтобы не итерироваться по ним без необходимости
        taskList.clear();
        for (Task task : tasks.values())
            taskList.add(toString(task));

        for (Epic epic : epics.values())
            taskList.add(toString(epic));

        for (SubTask subTask : subTasks.values())
            taskList.add(toString(subTask));
    }

    private void writeDown() throws IOException {
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
        String result = task.getId() + "," +
                type + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription();

        if (type == Type.SUBTASK) {
            SubTask sTask = (SubTask) task;
            Integer parent = sTask.getParentEpic();
            if (parent != null)
                result += "," + parent;
        }
        return result;
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

        switch (type) {
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setStatus(status);
                addEpic(epic, taskId);
                return epic;
            case SUBTASK:
                SubTask subTask = new SubTask(name, description);
                subTask.setStatus(status);
                addSubTask(subTask, taskId);
                if (taskStr.length == 6)
                    linkSubToEpic(subTask,
                            epics.get(Integer.parseInt(taskStr[5])));
                return subTask;
            default:
                Task task = new Task(name, description);
                task.setStatus(status);
                addTask(task, taskId);
                return task;
        }
    }

    public static List<Integer> historyFromString(String strHistory) {
        List<Integer> h = new ArrayList<>();
        for (String id : strHistory.split(",")) {
            h.add(Integer.parseInt(id));
        }
        return h;
    }

    private void loadHistory(List<Integer> history) {
        for (int item : history) {

            if (tasks.get(item) != null)
                this.history.add(tasks.get(item));

            else if (epics.get(item) != null)
                this.history.add((epics.get(item)));

            else if (subTasks.get(item) != null)
                this.history.add(subTasks.get(item));
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager();
        try (Reader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
            List<String> content = getContent(br);
            for (String elem : content) {
                //String[] item = elem.split(",");
                if (elem.isEmpty()) {
                    manager.loadHistory(historyFromString(content.get(content.size() - 1)));
                    break;
                }
                manager.fromString(elem);
            }
            manager.save();
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            return null;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
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
