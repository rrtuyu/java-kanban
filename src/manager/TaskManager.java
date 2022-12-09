package manager;

import task.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class TaskManager {
    private int id;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;

    public TaskManager() {
        id = 1;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    //Создание
    public void addTask(Task task) {
        if (task == null) {
            System.out.println("Empty object can't be added");
            return;
        }
        tasks.put(id, task);
        task.setId(id);
        id++;
    }

    public void addEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Empty object can't be added");
            return;
        }
        epics.put(id, epic);
        epic.setId(id);
        id++;
    }

    public void addSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("Empty object can't be added");
            return;
        }
        subTasks.put(id, subTask);
        subTask.setId(id);
        id++;
    }

    public void linkSubToEpic(SubTask subTask, Epic epic){
        subTask.linkToEpic(epic.getId());
        epic.addSubTask(subTask.getId());
    }

    //Получение списка всех задач
    public List<Task> getTasks() {
        if (tasks.isEmpty()) {
            System.out.println("There are no tasks");
            return null;
        }
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getEpics() {
        if (epics.isEmpty()) {
            System.out.println("There are no epics");
            return null;
        }
        return new ArrayList<>(epics.values());
    }

    public List<SubTask> getSubTasks() {
        if (subTasks.isEmpty()) {
            System.out.println("There are no sub tasks");
            return null;
        }
        return new ArrayList<>(subTasks.values());
    }

    //Удаление всех задач
    public void clearTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Task list is already clear");
            return;
        }
        tasks.clear();
    }

    public void clearEpics() {
        if (epics.isEmpty()) {
            System.out.println("Epic list is already clear");
            return;
        }
        for (Epic epic : epics.values()) {
            if (epic.getSubTasks() != null) {
                clearSubTasks(epic.getSubTasks());
                epic.clear();
            }
            epics.clear();
        }
    }

    public void clearSubTasks() {
        if (subTasks.isEmpty()) {
            System.out.println("Sub task list is already empty");
            return;
        }
        for (SubTask subTask : subTasks.values()) {
            Integer parent = subTask.getParentEpic();
            if (parent != null) {
                Epic currentParent = epics.get(parent);
                currentParent.removeSubtask(subTask.getId());
                updateEpic(currentParent);
            }
        }
        subTasks.clear();
    }

    //вспомогательный метод для очистки списка эпиков
    private void clearSubTasks(List<Integer> idList) {
        for (Integer id : idList) {
            subTasks.remove(id);
        }
    }

    //Получение по идентификатору
    public Task getTask(int id) {
        if (tasks.isEmpty()) {
            System.out.println("Task list is empty");
            return null;
        }
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else {
            System.out.println("There is no task with id=" + id);
            return null;
        }
    }

    public Epic getEpic(int id) {
        if (epics.isEmpty()) {
            System.out.println("Epic list is empty");
            return null;
        }
        if (epics.containsKey(id)) {
            return epics.get(id);
        } else {
            System.out.println("There is no epic with id=" + id);
            return null;
        }
    }

    public SubTask getSubTask(int id) {
        if (subTasks.isEmpty()) {
            System.out.println("Sub task list is empty");
            return null;
        }
        if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        } else {
            System.out.println("There is no subTask with id=" + id);
            return null;
        }
    }

    //Удаление по идентификатору
    public void removeTask(int id) {
        if (tasks.isEmpty()) {
            System.out.println("Task list is empty");
            return;
        }
        if (tasks.containsKey(id))
            tasks.remove(id);
        else
            System.out.println("There is no task with id=" + id);

    }

    public void removeEpic(int id) {
        if (epics.isEmpty()) {
            System.out.println("Epic list is empty");
            return;
        }
        if (epics.containsKey(id)) {
            if (epics.get(id).getSubTasks() != null)
                clearSubTasks(epics.get(id).getSubTasks());
            epics.get(id).clear();
            epics.remove(id);
        } else
            System.out.println("There is no epic with id=" + id);
    }

    public void removeSubTask(int id) {
        if (subTasks.isEmpty()) {
            System.out.println("Sub task list is empty");
            return;
        }
        if (subTasks.containsKey(id)) {
            Integer currentParent = subTasks.get(id).getParentEpic();
            if (currentParent != null) {
                epics.get(currentParent).removeSubtask(id);
                updateEpic(epics.get(currentParent));
            }
            subTasks.remove(id);
        } else
            System.out.println("There is no sub task with id=" + id);
    }

    public void update(Task task, int id) {
        if (task == null) {
            System.out.println("Empty object can't be updated");
            return;
        }
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
            task.setId(id);
        } else
            System.out.println("No such a task in tracking, unable to update");
    }

    public void update(SubTask subTask, int id) {
        if (subTask == null) {
            System.out.println("Empty object can't be updated");
            return;
        }
        if (subTasks.containsKey(id)) {
            int parent = subTasks.get(id).getParentEpic();
            subTasks.put(id, subTask);
            subTask.setId(id);
            subTask.linkToEpic(parent);
            updateEpic(epics.get(parent));
        } else
            System.out.println("No such a sub task in tracking, unable to update");
    }

    public void update(Epic epic, int id) {
        if (epic == null) {
            System.out.println("Empty object can't be updated");
            return;
        }
        if (epics.containsKey(id)) {
            epics.put(id, epic);
            epic.setId(id);
        } else
            System.out.println("No such an epic in tracking, unable to update");
    }

    private void updateEpic(Epic epic) {
        int doneCounter = 0;
        List<Integer> subTasks = getSubTasksOf(epic);
        if (subTasks.isEmpty()) {
            epic.setStatus(Task.statusNew);
            return;
        }
        for (int subTask : subTasks) {
            String subStatus = this.subTasks.get(subTask).getStatus();
            if (subStatus.equals(Task.statusInProgress)) {
                epic.setStatus(Task.statusInProgress);
                return;
            }
            if (subStatus.equals(Task.statusDone))
                doneCounter++;
        }
        if (doneCounter == subTasks.size())
            epic.setStatus(Task.statusDone);
        else if (doneCounter > 0)
            epic.setStatus(Task.statusInProgress);
        else
            epic.setStatus(Task.statusNew);
    }

    public List<Integer> getSubTasksOf(Epic epic) {
        if (epic == null) {
            System.out.println("This epic os empty");
            return null;
        }
        if (epics.containsValue(epic))
            return epic.getSubTasks();
        else {
            System.out.println("No such an epic in tracking, invalid operation");
            return null;
        }
    }
}