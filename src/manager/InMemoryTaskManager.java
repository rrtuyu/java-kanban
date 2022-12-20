package manager;

import task.*;

import java.util.*;

public class InMemoryTaskManager<T extends Task> implements TaskManager {
    private int id;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;

    private static final byte HISTORY_CAPACITY = 10;
    private List<T> history;

    public InMemoryTaskManager() {
        id = 1;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        history = new ArrayList<>(HISTORY_CAPACITY);
    }

    //Создание

    @Override
    public void addTask(Task task) {
        if (task == null) {
            System.out.println("Empty object can't be added");
            return;
        }
        tasks.put(id, task);
        task.setId(id);
        id++;
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Empty object can't be added");
            return;
        }
        epics.put(id, epic);
        epic.setId(id);
        id++;
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("Empty object can't be added");
            return;
        }
        subTasks.put(id, subTask);
        subTask.setId(id);
        id++;
    }

    @Override
    public void linkSubToEpic(SubTask subTask, Epic epic) {
        subTask.linkToEpic(epic.getId());
        epic.addSubTask(subTask.getId());
    }

    //Получение списка всех задач
    @Override
    public List<Task> getTasks() {
        if (tasks.isEmpty()) {
            System.out.println("There are no tasks");
            return null;
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        if (epics.isEmpty()) {
            System.out.println("There are no epics");
            return null;
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        if (subTasks.isEmpty()) {
            System.out.println("There are no sub tasks");
            return null;
        }
        return new ArrayList<>(subTasks.values());
    }

    //Удаление всех задач
    @Override
    public void clearTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Task list is already clear");
            return;
        }
        tasks.clear();
    }

    @Override
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

    @Override
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
    @Override
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

    @Override
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

    @Override
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
    @Override
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

    @Override
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

    @Override
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

    @Override
    public void updateTask(Task task, int id) {
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

    @Override
    public void updateSubTask(SubTask subTask, int id) {
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

    @Override
    public void updateEpic(Epic epic, int id) {
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
        List<Integer> subTasks = epic.getSubTasks();
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

    @Override
    public List<SubTask> getSubTasksOf(Epic epic) {
        if (epic == null) {
            System.out.println("This epic is empty");
            return null;
        }
        if (epics.containsKey(epic.getId())) {
            ArrayList<SubTask> result = new ArrayList<>();
            List<Integer> idList = epic.getSubTasks();
            for (Integer id : idList)
                result.add(subTasks.get(id));
            return result;
        } else {
            System.out.println("No such an epic in tracking, invalid operation");
            return null;
        }
    }

    //обработка истории запросов
    @Override
    public List<T> getHistory() {
        return history;
    }

    private void historyAdd(T task) {
        history.add(task);
        historyCheck();
    }

    private void historyCheck() {
        if (history.size() > HISTORY_CAPACITY){
            history.remove(0);
            for (int i = 0; i < history.size(); i++)
                Collections.swap(history, i + 1, i);
        }
    }
}