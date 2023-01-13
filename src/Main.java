import manager.Managers;
import manager.TaskManager;
import task.*;

public class Main {

    public static void main(String[] args) {
        TaskManager tm = Managers.getDefault();

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
        tm.linkSubToEpic(subTask3, epic1);


        // ТЕСТ ИСКЛЮЧЕНИЯ ПОВТОРЕНИЙ
        System.out.println("Тест истории\ntm.getEpic(1);");
        tm.getEpic(1);
        System.out.println(tm.getHistory());

        System.out.println("Тест истории\ntm.getSubTask(3);");
        tm.getSubTask(3);
        System.out.println(tm.getHistory());

        System.out.println("Тест истории\ntm.getSubTask(4);");
        tm.getSubTask(4);
        System.out.println(tm.getHistory());

        System.out.println("Тест истории\ntm.getSubTask(5);");
        tm.getSubTask(5);
        System.out.println(tm.getHistory());

        System.out.println("Тест истории\ntm.getEpic(1);");
        tm.getEpic(1);
        System.out.println(tm.getHistory());

        System.out.println("Тест истории\ntm.getEpic(2);");
        tm.getEpic(2);
        System.out.println(tm.getHistory());

        System.out.println("Тест истории\ntm.getEpic(1);");
        tm.getEpic(1);
        System.out.println(tm.getHistory());

        //ТЕСТ УДАЛЕНИЯ ИЗ ИСТОРИИ НЕСУЩЕСТВУЮЩИХ ЭЛЕМЕНТОВ
        System.out.println("Тест истории\ntm.removeSubTask(3);");
        tm.removeSubTask(3);
        System.out.println(tm.getHistory());

        System.out.println("Тест истории\ntm.removeEpic(1);");
        tm.removeEpic(1);
        System.out.println(tm.getHistory());

    }
}
