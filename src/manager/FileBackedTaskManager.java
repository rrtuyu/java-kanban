package manager;

import task.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private String file;
    private static final String HEADER = "id,type,name,status,description,epic";
    private final List<String> taskList;

    public FileBackedTaskManager(String file) {
        super();
        taskList = new ArrayList<>();
        this.file = file;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        updateTaskList();
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        updateTaskList();
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        updateTaskList();
        save();
    }

    @Override
    public void linkSubToEpic(SubTask subTask, Epic epic) {
        super.linkSubToEpic(subTask, epic);
        updateTaskList();
        save();
    }

    //TODO сделать метод для сохранения текущего состояние менеджера в файл
    private void save() {
        writeDown();
    }

    private void writeDown() throws IOException{
        try (Writer writer = new FileWriter(file, false); BufferedWriter bw = new BufferedWriter(writer)) {
            bw.write(HEADER + "\n");

            for (String task : taskList)
                bw.write(task + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String taskToString(Task task) {
        Type type = task.getType();
        String result = task.getId() + "," +
                type + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + ",";

        if (type == Type.SUBTASK) {
            SubTask sTask = (SubTask) task;
            Integer parent = sTask.getParentEpic();
            if (parent != null)
                result += parent;
        }
        return result;
    }

    private Task taskFromString(String value){

    }

    private void updateTaskList() { //очень на нравится эта функция, но пока ничего другого не придумать не могу
        taskList.clear();
        for (Task task : tasks.values())
            taskList.add(taskToString(task));

        for (Epic epic : epics.values())
            taskList.add(taskToString(epic));

        for (SubTask subTask : subTasks.values())
            taskList.add(taskToString(subTask));
    }

    private static String historyToString(HistoryManager manager){

    }
}
