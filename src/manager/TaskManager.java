package manager;


import task.*;
import java.util.List;

public interface TaskManager<T extends Task> {
    //Создание
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask);

    void linkSubToEpic(SubTask subTask, Epic epic);

    //Получение списка всех задач
    List<Task> getTasks();

    List<Epic> getEpics();

    List<SubTask> getSubTasks();

    //Удаление всех задач
    void clearTasks();

    void clearEpics();

    void clearSubTasks();

    //Получение по идентификатору
    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    //Удаление по идентификатору
    void removeTask(int id);

    void removeEpic(int id);

    void removeSubTask(int id);

    //возможно пойдет под нож
    void updateTask(Task task, int id);

    void updateSubTask(SubTask subTask, int id);

    void updateEpic(Epic epic, int id);

    List<SubTask> getSubTasksOf(Epic epic);

    List<T> getHistory();
}