import manager.Managers;
import manager.TaskManager;
import task.*;

public class Main {

    public static void main(String[] args) {
        TaskManager tm = Managers.getDefault();

        Task task1 = new Task("name1", "description1");
        Task task2 = new Task("name2", "description2");
        tm.addTask(task1);
        tm.addTask(task2);

        Epic epic1 = new Epic("name1", "description1");
        Epic epic2 = new Epic("name2", "description2");
        tm.addEpic(epic1);
        tm.addEpic(epic2);

        SubTask subTask1 = new SubTask("name1", "description1");
        SubTask subTask2 = new SubTask("name2", "description2");
        SubTask subTask3 = new SubTask("name3", "description3");
        tm.addSubTask(subTask1);
        tm.linkSubToEpic(subTask1, epic1);

        tm.addSubTask(subTask2);
        tm.linkSubToEpic(subTask2, epic1);

        tm.addSubTask(subTask3);
        tm.linkSubToEpic(subTask3, epic2);

        System.out.println(tm.getTasks());
        System.out.println(tm.getEpics());
        System.out.println(tm.getSubTasks());

        SubTask subTask1Update = new SubTask("updated name", "updated description");
        subTask1Update.setStatus(Status.DONE);
        tm.updateSubTask(subTask1Update, 5);

        SubTask subTask3Update = new SubTask("asdasd", "gogigagagagigo");
        subTask3Update.setStatus(Status.DONE);
        tm.updateSubTask(subTask3Update, 7);

        System.out.println("\n\n AFTER UPDATE");
        System.out.println(tm.getTasks());
        System.out.println(tm.getEpics());
        System.out.println(tm.getSubTasks());

        tm.getTask(1);
        tm.getSubTask(7);
        tm.getTask(1);

        System.out.println(tm.getHistory());

    }
}
