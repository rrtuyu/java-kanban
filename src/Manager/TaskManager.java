package Manager;

import Task.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;

    public TaskManager(){
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    //Создание
    //"Сам объект должен передаваться в качестве параметра" не совсем понял, от куда он должен передаваться и зачем,
    //поэтому сделал 2 набора таск пушеров на всякий случай
    public void addTask(Task task){
        if (task == null){
            System.out.println("Empty object can't be added");
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic){
        if (epic == null){
            System.out.println("Empty object can't be added");
            return;
        }
        epics.put(epic.getId(), epic);
    }

    public void addSubTask(SubTask subTask, Epic parent){
        if (subTask == null || parent == null){
            System.out.println("Empty object can't be added");
            return;
        }
        parent.addSubTask(subTask);
        subTasks.put(subTask.getId(), subTask);
    }

    public void createTask(String name, String description){
        Task task = new Task(name, description);
        tasks.put(task.getId(), task);
    }

    public void createEpic(String name, String description){
        Epic epic = new Epic(name, description);
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(String name, String description, int parentId){
        SubTask subTask;
        if (epics.containsKey(parentId)) {
            subTask = new SubTask(name, description, epics.get(parentId));
            epics.get(parentId).addSubTask(subTask);
            subTasks.put(subTask.getId(), subTask);
        } else
            System.out.println("There is no parent with id=" + parentId + "\nUnable to create a sub task)");
    }

    //Получение списка всех задач
    public ArrayList<Task> getTasks(){
        if (tasks.isEmpty()){
            System.out.println("There are no tasks");
            return null;
        }
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics(){
        if (epics.isEmpty()){
            System.out.println("There are no epics");
            return null;
        }
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getSubTasks(){
        if (subTasks.isEmpty()){
            System.out.println("There are no sub tasks");
            return null;
        }
        return new ArrayList<>(subTasks.values());
    }

    //Удаление всех задач
    public void clearTasks(){
        if(tasks.isEmpty()){
            System.out.println("Task list is already clear");
            return;
        }
        tasks.clear();
    }

    public void clearEpics(){
        if(epics.isEmpty()){
            System.out.println("Epic list is already clear");
            return;
        }
        for (Epic epic : epics.values()) {
            if (epic.getSubTasks() != null){
                clearSubTasks(epic.getSubTasks());
                epic.clear();
            }
            epics.clear();
        }
    }

    public void clearSubTasks(){
        if (subTasks.isEmpty()){
            System.out.println("Sub task list is already clear");
            return;
        }
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getParentEpic() != null)
                subTask.getParentEpic().removeSubtask(subTask);
        }
        subTasks.clear();
    }

    private void clearSubTasks(ArrayList<Integer> idList){
        for (Integer id : idList) {
            subTasks.remove(id);
        }
    } //вспомогательный метод

    //Получение по идентификатору
    public Task getTask(int id){
        if (tasks.isEmpty()){
            System.out.println("Task list is empty");
            return null;
        }
        if (tasks.containsKey(id)){
            return tasks.get(id);
        } else {
            System.out.println("There is no task with id=" + id);
            return null;
        }
    }

    public Epic getEpic(int id){
        if (epics.isEmpty()){
            System.out.println("Epic list is empty");
            return null;
        }
        if (epics.containsKey(id)){
            return epics.get(id);
        } else {
            System.out.println("There is no epic with id=" + id);
            return null;
        }
    }

    public SubTask getSubTask(int id){
        if(subTasks.isEmpty()){
            System.out.println("Sub task list is empty");
            return null;
        }
        if(subTasks.containsKey(id)){
            return subTasks.get(id);
        } else {
            System.out.println("There is no subTask with id=" + id);
            return null;
        }
    }

    //Удаление по идентификатору
    public void removeTask(int id){
        if(tasks.isEmpty()){
            System.out.println("Task list is empty");
            return;
        }
        if(tasks.containsKey(id))
            tasks.remove(id);
        else
            System.out.println("There is no task with id=" + id);

    }

    public void removeEpic(int id){
        if (epics.isEmpty()){
            System.out.println("Epic list is empty");
            return;
        }
        if (epics.containsKey(id)){
            if (epics.get(id).getSubTasks() != null)
                clearSubTasks(epics.get(id).getSubTasks());
            epics.get(id).clear();
        } else
            System.out.println("There is no epic with id=" + id);
    }

    public void removeSubTask(int id){
        if (subTasks.isEmpty()){
            System.out.println("Sub task list is empty");
            return;
        }
        if (subTasks.containsKey(id)){
            if (subTasks.get(id).getParentEpic() != null)
                subTasks.get(id).getParentEpic().removeSubtask(subTasks.get(id));
            subTasks.remove(id);
        } else
            System.out.println("There is no sub task with id=" + id);
    }

    public void update(Object o, String status){
        if(o == null){
            System.out.println("Empty object can't be updated");
            return;
        }
        if (o.getClass() == Task.class){
            if (tasks.values().contains(o))

                ((Task) o).updateStatus(status);
            else
                System.out.println("No such a task in tracking, unable to update");
        } else if (o.getClass() == SubTask.class){
            if (subTasks.values().contains(o))
                ((SubTask) o).updateStatus(status);
            else
                System.out.println("No such a sub task in tracking, unable to update");
        } else
            System.out.println("You can't update this type of task\nOnly \'Task\' and \'SubTask\' can be updated");
    }
}