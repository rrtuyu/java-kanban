import Manager.TaskManager;
import Task.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        taskManager.createTask("Погладить кота", "НЕ ГЛАДИТЬ ПРОТИВ ШЕРСТИ, ОН КУСАЕТСЯ!!!");

        taskManager.createEpic("ЖЫЗЕНЬ", "КАК ЖЫТЬ");
        Epic epic = new Epic("Очень странные дела", "Кто-то украл мозг");
        taskManager.addEpic(epic);//не знаю зачем, просто так можно

        taskManager.createSubtask("Проснуться", "Очень сложно", 2);
        taskManager.createSubtask("Покушать", "Очень приятно", 2);
        SubTask subTask = new SubTask("Найти мозг", "Посмотреть в под столом, может укатился", epic);
        taskManager.addSubTask(subTask, epic);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());

        taskManager.update(taskManager.getTask(1), Task.statusInProgress);
        taskManager.update(taskManager.getSubTask(4), Task.statusDone);
        taskManager.update(subTask, Task.statusDone);

        System.out.println("after updating");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());
    }
}
