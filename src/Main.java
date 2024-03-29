import kvserver.KVServer;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.SubTask;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            new KVServer().start();
        } catch (IOException e) {
            System.out.println("CHTO-TO POSHLO NE TAK :(");
        }

        TaskManager tm = Managers.getDefault();

        Epic epic1 = new Epic("name1", "description1");
        Epic epic2 = new Epic("name2", "description2");
        SubTask subTask1 = new SubTask("name1", "description1");
        SubTask subTask2 = new SubTask("name2", "description2");
        SubTask subTask3 = new SubTask("name3", "description3");

        tm.addEpic(epic1);

        tm.addSubTask(subTask1);

        tm.addEpic(epic2);


        tm.linkSubToEpic(subTask1, epic1);

        tm.addSubTask(subTask2);
        tm.linkSubToEpic(subTask2, epic1);

        tm.addSubTask(subTask3);
        tm.linkSubToEpic(subTask3, epic2);

        tm.getEpic(1);

        System.out.println(tm.getHistory());
    }
}
