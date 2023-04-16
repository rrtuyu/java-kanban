package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

class InMemoryTaskManager implements TaskManager {
    protected int id;
    protected Map<Integer, Task> tasks;
    protected Map<Integer, Epic> epics;
    protected Map<Integer, SubTask> subTasks;
    protected TreeSet<Task> priorityTaskSet;
    protected HistoryManager history;

    public InMemoryTaskManager() {
        id = 1;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        priorityTaskSet = new TreeSet<>(
                (t1, t2) -> {
                    LocalDateTime t1Start = t1.getStartTime();
                    LocalDateTime t2Start = t2.getStartTime();
                    if (t1Start == null || t1Start.isEqual(LocalDateTime.MAX))
                        return 1;
                    if (t2Start == null || t2Start.isEqual(LocalDateTime.MAX))
                        return -1;

                    if (t1.equals(t2))
                        return 0;

                    long deltaTime = Duration.between(t1Start, t2Start).toMinutes();
                    if (deltaTime > 0)
                        return -1;
                    else
                        return 1;
                });
        history = Managers.getDefaultHistory();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o.getClass() != this.getClass()) return false;
        TaskManager otherManager = (TaskManager) o;
        return hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int hash = 17;
        hash = prime * hash + tasks.hashCode();
        hash = prime * hash + epics.hashCode();
        hash = prime * hash + subTasks.hashCode();
        hash = prime * hash + getHistory().hashCode();
        return hash;
    }


    //Создание

    @Override
    public void addTask(Task task) throws IllegalArgumentException {
        if (task == null)
            throw new IllegalArgumentException("Empty object cannot be added");

        if (!task.hasId()) {
            task.setId(id);
            addToPriorityList(task);
            tasks.put(id, task);
            id++;
        } else {
            int outerId = task.getId();

            if (checkIdCollision(outerId))
                throw new IllegalArgumentException("Manager already has this instance");

            addToPriorityList(task);
            tasks.put(outerId, task);
            if (outerId > id) {
                id = outerId + 1;
                ++id;
            }
        }
    }

    @Override
    public void addEpic(Epic epic) throws IllegalArgumentException {
        if (epic == null)
            throw new IllegalArgumentException("Empty object cannot be added");

        if (!epic.hasId()) {
            epic.setId(id);
            epics.put(id, epic);
            id++;
        } else {
            int outerId = epic.getId();

            if (checkIdCollision(outerId))
                throw new IllegalArgumentException("Manager already has this instance");

            epics.put(outerId, epic);
            if (outerId > id) {
                id = outerId + 1;
                ++id;
            }
        }
    }

    @Override
    public void addSubTask(SubTask subTask) throws IllegalArgumentException {
        if (subTask == null)
            throw new IllegalArgumentException("Empty object cannot be added");

        if (!subTask.hasId()) {
            subTask.setId(id);
            addToPriorityList(subTask);
            subTasks.put(id, subTask);
            id++;
        } else {
            int outerId = subTask.getId();

            if (checkIdCollision(outerId))
                throw new IllegalArgumentException("Manager already has this instance");

            addToPriorityList(subTask);
            subTasks.put(subTask.getId(), subTask);
            if (outerId > id) {
                id = outerId + 1;
                ++id;
            }
        }
    }

    @Override
    public void linkSubToEpic(SubTask subTask, Epic epic) {
        subTask.linkToEpic(epic.getId());
        epic.addSubTask(subTask.getId());
        updateEpicDuration(epic);
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
        for (int task : tasks.keySet()) {
            priorityTaskSet.remove(tasks.get(task));
            history.remove(task);
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
            history.remove(epic.getId());
        }
        epics.clear();
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
            priorityTaskSet.remove(subTask);
            history.remove(subTask.getId());
        }
        subTasks.clear();
    }

    //вспомогательный метод для очистки списка эпиков
    private void clearSubTasks(List<Integer> idList) {
        for (Integer id : idList) {
            priorityTaskSet.remove(subTasks.get(id));
            subTasks.remove(id);
            history.remove(id);
        }
    }

    //Получение по идентификатору
    @Override
    public Task getTask(int id) throws IllegalArgumentException {
        if (tasks.isEmpty())
            throw new IllegalArgumentException("Task list is empty");

        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            history.add(task);
            return task;
        } else {
            throw new IllegalArgumentException("There is no task with id=" + id);
        }
    }

    @Override
    public Epic getEpic(int id) throws IllegalArgumentException {
        if (epics.isEmpty())
            throw new IllegalArgumentException("Epic list is empty");

        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            history.add(epic);
            return epic;
        } else {
            throw new IllegalArgumentException("There is no epic with id=" + id);
        }
    }

    @Override
    public SubTask getSubTask(int id) throws IllegalArgumentException {
        if (subTasks.isEmpty())
            throw new IllegalArgumentException("Sub task list is empty");

        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            history.add(subTask);
            return subTask;
        } else {
            throw new IllegalArgumentException("There is no subTask with id=" + id);
        }
    }

    //Удаление по идентификатору
    @Override
    public void removeTask(int id) throws IllegalArgumentException {
        if (tasks.isEmpty())
            throw new IllegalArgumentException("Task list is empty");

        if (tasks.containsKey(id)) {
            priorityTaskSet.remove(tasks.get(id));
            tasks.remove(id);
            history.remove(id);
        } else {
            throw new IllegalArgumentException("There is no task with id=" + id);
        }

    }

    @Override
    public void removeEpic(int id) throws IllegalArgumentException {
        if (epics.isEmpty())
            throw new IllegalArgumentException("Epic list is empty");

        if (epics.containsKey(id)) {
            List<Integer> subs = epics.get(id).getSubTasks();
            if (subs != null)
                clearSubTasks(subs);
            epics.get(id).clear();
            epics.remove(id);
            history.remove(id);
        } else {
            throw new IllegalArgumentException("There is no epic with id=" + id);
        }
    }

    @Override
    public void removeSubTask(int id) throws IllegalArgumentException {
        if (subTasks.isEmpty())
            throw new IllegalArgumentException("Sub task list is empty");

        if (subTasks.containsKey(id)) {
            Integer currentParent = subTasks.get(id).getParentEpic();
            if (currentParent != null) {
                epics.get(currentParent).removeSubtask(id);
                updateEpic(epics.get(currentParent));
            }
            priorityTaskSet.remove(subTasks.get(id));
            subTasks.remove(id);
            history.remove(id);
        } else {
            throw new IllegalArgumentException("There is no sub task with id=" + id);
        }
    }

    @Override
    public void updateTask(Task task, int id) throws IllegalArgumentException {
        if (task == null)
            throw new IllegalArgumentException("Empty object can't be updated");

        if (tasks.containsKey(id)) {
            tasks.put(id, task);
            task.setId(id);
            addToPriorityList(task);
        } else {
            throw new IllegalArgumentException("No such a task in tracking, unable to update");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask, int id) throws IllegalArgumentException {
        if (subTask == null)
            throw new IllegalArgumentException("Empty object can't be updated");

        if (subTasks.containsKey(id)) {
            int parent = subTasks.get(id).getParentEpic();
            subTasks.put(id, subTask);
            subTask.setId(id);
            subTask.linkToEpic(parent);
            updateEpic(epics.get(parent));
            addToPriorityList(subTask);
        } else {
            throw new IllegalArgumentException("No such a sub task in tracking, unable to update");
        }
    }

    @Override
    public void updateEpic(Epic epic, int id) throws IllegalArgumentException {
        if (epic == null)
            throw new IllegalArgumentException("Empty object can't be updated");

        if (epics.containsKey(id)) {
            epics.put(id, epic);
            epic.setId(id);
            updateEpicDuration(epic);
        } else {
            throw new IllegalArgumentException("No such an epic in tracking, unable to update");
        }
    }

    private void updateEpic(Epic epic) {
        int doneCounter = 0;
        List<Integer> subTasks = epic.getSubTasks();
        if (subTasks.isEmpty()) {
            epic.resetTimings();
            epic.setStatus(Status.NEW);
            return;
        }
        for (int subTask : subTasks) {
            Status subStatus = this.subTasks.get(subTask).getStatus();
            if (!subStatus.equals(Status.NEW)) {
                epic.setStatus(Status.IN_PROGRESS);
            }
            if (subStatus.equals(Status.DONE))
                doneCounter++;
        }
        if (doneCounter == subTasks.size())
            epic.setStatus(Status.DONE);
        else if (doneCounter > 0)
            epic.setStatus(Status.IN_PROGRESS);
        updateEpicDuration(epic);
    }

    @Override
    public List<SubTask> getSubTasksOf(Epic epic) throws IllegalArgumentException {
        if (epic == null)
            throw new IllegalArgumentException("This epic is empty");

        if (epics.containsKey(epic.getId())) {
            ArrayList<SubTask> result = new ArrayList<>();
            List<Integer> idList = epic.getSubTasks();
            for (Integer id : idList)
                result.add(subTasks.get(id));
            return result;
        } else {
            throw new IllegalArgumentException("No such an epic in tracking, invalid operation");
        }
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return priorityTaskSet;
    }

    private void addToPriorityList(Task task) throws IllegalArgumentException {
        if (task.getStartTime() != null && task.getEndTime() != null) {
            LocalDateTime taskStartTime = task.getStartTime();
            LocalDateTime taskEndTime = task.getEndTime();

            for (Task item : priorityTaskSet) {
                if (checkTimeCollision(task, item) && !task.equals(item))
                    throw new IllegalArgumentException("Tasks' runtime should not collide." +
                            " Task id: " + task.getId() +
                            " collides with task id: " + item.getId());
            }
        }
        priorityTaskSet.add(task);
    }

    private boolean checkTimeCollision(Task t1, Task t2) { //true if colliding, false if not
        LocalDateTime t1StartTime = t1.getStartTime();
        LocalDateTime t1EndTime = t1.getEndTime();

        LocalDateTime t2StartTime = t2.getStartTime();
        LocalDateTime t2EndTime = t2.getEndTime();

        if (t1StartTime == null || t2StartTime == null)
            return false;

        if (t2StartTime.isBefore(t1StartTime))
            return !t2EndTime.isBefore(t1StartTime);
        else if (t2StartTime.isAfter(t1StartTime))
            return !t2StartTime.isAfter(t1EndTime);
        else
            return t2StartTime.isEqual(t1StartTime) || t2EndTime.isEqual(t1EndTime);
    }

    private boolean checkIdCollision(int id) { //true if colliding, false if not
        List<Integer> idList = new ArrayList<>();
        tasks.keySet().forEach(idList::add);
        epics.keySet().forEach(idList::add);
        subTasks.keySet().forEach(idList::add);

        return idList.contains(id);
    }

    private void updateEpicDuration(Epic epic) {
        epic.resetTimings();
        for (SubTask sub : getSubTasksOf(epic)) {
            LocalDateTime subStart = sub.getStartTime();
            LocalDateTime subEnd = sub.getEndTime();
            if (subStart != null && subEnd != null)
                epic.setDuration(subStart, Duration.between(subStart, subEnd).toMinutes());
        }
    }
}