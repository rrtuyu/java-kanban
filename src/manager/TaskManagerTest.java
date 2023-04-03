package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T manager;
    Task task1;
    Task task2;

    Epic epic1;

    SubTask subTask1;
    SubTask subTask2;

    abstract T createNewManager();

    @BeforeEach
    void taskSetInit() {
        task1 = new Task("test task 1", "test task description 1");
        task2 = new Task("test task 2", "test task description 2");

        epic1 = new Epic("test epic 1", "test epic description 1");

        subTask1 = new SubTask("test subtask 1", "test subtask description 1");
        subTask2 = new SubTask("test subtask 2", "test subtask description 2");

        manager = createNewManager();

        manager.addTask(task1); // id 1
        manager.addTask(task2); // id 2

        manager.addEpic(epic1); // id 3

        manager.addSubTask(subTask1); // id 4
        manager.addSubTask(subTask2); // id 5

        manager.linkSubToEpic(subTask1, epic1);
        manager.linkSubToEpic(subTask2, epic1);
    }

    //test set for Epic
    @Test
    void addEpicTest() {
        Epic newEpic = new Epic("new epic", "new scription");

        manager.addEpic(newEpic);
        List<Epic> expected = List.of(epic1, newEpic);

        assertIterableEquals(expected, manager.getEpics());
    }

    @Test
    void shouldNotAddNullEpic() {
        List<Epic> expected = manager.getEpics();

        manager.addEpic(null);
        assertEquals(expected, manager.getEpics());
    }

    @Test
    void updateEpicTest() {
        Epic newEpic = new Epic("new epic", "new descroption");

        manager.updateEpic(newEpic, 3);
        assertEquals(newEpic, manager.getEpic(3));
    }

    @Test
    void shouldNotUpdateEpicWhenNull() {
        manager.updateEpic(null, 3);

        assertEquals(epic1, manager.getEpic(3));
    }

    @Test
    void shouldReturnNewWhenEverySubIsNew() {
        Status expected = Status.NEW;

        subTask1.setStatus(Status.NEW);
        subTask2.setStatus(Status.NEW);

        manager.updateSubTask(subTask1, 4);
        manager.updateSubTask(subTask2, 5);

        assertEquals(expected, manager.getEpic(3).getStatus());
    }

    @Test
    void shouldReturnDoneWhenEverySubIsDone() {
        Status expected = Status.DONE;

        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);

        manager.updateSubTask(subTask1, 4);
        manager.updateSubTask(subTask2, 5);

        assertEquals(expected, manager.getEpic(3).getStatus());
    }

    @Test
    void shouldReturnIn_ProgressWhenEverySubIsIn_Progress() {
        Status expected = Status.IN_PROGRESS;

        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.IN_PROGRESS);

        manager.updateSubTask(subTask1, 4);
        manager.updateSubTask(subTask2, 5);

        assertEquals(expected, manager.getEpic(3).getStatus());
    }

    @Test
    void shouldReturnIn_ProgressWnenSubsAreInDifferentStatusDone_New() {
        Status expected = Status.IN_PROGRESS;

        subTask2.setStatus(Status.DONE);
        manager.updateSubTask(subTask2, 5);

        assertEquals(expected, manager.getEpic(3).getStatus());
    }

    @Test
    void shouldReturnIn_ProgressWnenSubsAreInDifferentStatusInProgress_New() {
        Status expected = Status.IN_PROGRESS;

        subTask2.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask2, 5);

        assertEquals(expected, manager.getEpic(3).getStatus());
    }

    @Test
    void shouldReturnIn_ProgressWnenSubsAreInDifferentStatusInProgress_Done() {
        Status expected = Status.IN_PROGRESS;

        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.IN_PROGRESS);

        manager.updateSubTask(subTask1, 4);
        manager.updateSubTask(subTask2, 5);

        assertEquals(expected, manager.getEpic(3).getStatus());
    }

    @Test
    void shouldReturnNewWhenEmpty() {
        Status expected = Status.NEW;

        subTask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask1, 4);

        manager.clearSubTasks();

        assertEquals(expected, manager.getEpic(3).getStatus());
    }

    @Test
    void shouldReturnListOfSubTasksWhenHasSubTasks() {
        List<SubTask> expected = List.of(subTask1, subTask2);

        assertIterableEquals(expected, manager.getSubTasksOf(epic1));
    }

    @Test
    void shouldReturnEmptyListWhenNoSubTasksAreLinked() {
        manager.clearSubTasks();

        assertTrue(manager.getSubTasksOf(epic1).isEmpty());
    }

    @Test
    void shouldReturnListOfEpics() {
        List<Epic> expected = List.of(epic1);

        assertIterableEquals(expected, manager.getEpics());
    }

    @Test
    void shouldReturnNullWhenNoEpics() {
        manager.clearEpics();

        assertNull(manager.getEpics());
    }

    //test set for SubTask
    @Test
    void addSubTaskTest() {
        SubTask newSub = new SubTask("new sub", "new scription");

        manager.addSubTask(newSub);
        List<SubTask> expected = List.of(subTask1, subTask2, newSub);

        assertIterableEquals(expected, manager.getSubTasks());
    }

    @Test
    void shouldNotAddNullSub() {
        List<SubTask> expected = manager.getSubTasks();

        manager.addSubTask(null);
        assertEquals(expected, manager.getSubTasks());
    }

    @Test
    void updateSubTaskTest() {
        SubTask newSub = new SubTask("new sub", "new description");

        manager.updateSubTask(newSub, 4);

        assertEquals(newSub, manager.getSubTask(4));
    }

    @Test
    void shouldNotSubTaskUpdateWhenNull() {
        manager.updateSubTask(null, 4);

        assertEquals(subTask1, manager.getSubTask(4));
    }

    @Test
    void shouldReturnParentEpicID() {
        int expected = epic1.getId();

        assertEquals(expected, manager.getSubTask(4).getParentEpic());
        assertEquals(expected, manager.getSubTask(5).getParentEpic());
    }

    @Test
    void shouldReturnNullWhenNoParent() {
        SubTask testSub = new SubTask("aaa test", "bbb test");

        manager.addSubTask(testSub); //id 6

        assertNull(manager.getSubTask(6).getParentEpic());
    }

    @Test
    void shouldReturnNulWhenParentEpicIsRemoved() {
        manager.removeEpic(3);

        assertNull(manager.getSubTask(4));
        assertNull(manager.getSubTask(5));
    }

    @Test
    void shouldReturnNullWhenNoSubTasks() {
        manager.clearSubTasks();

        assertNull(manager.getSubTasks());
    }

    @Test
    void removeSubTaskTest() {
        List<SubTask> expected = List.of(subTask2);

        manager.removeSubTask(4);
        assertIterableEquals(expected, manager.getSubTasks());
    }

    //test set for Task
    @Test
    void addTaskTest() {
        Task newTask = new Task("new task", "new scription");

        manager.addTask(newTask);
        List<Task> expected = List.of(task1, task2, newTask);

        assertIterableEquals(expected, manager.getTasks());
    }

    @Test
    void shouldNotAddNullTask() {
        List<Task> expected = manager.getTasks();

        manager.addTask(null);
        assertEquals(expected, manager.getTasks());
    }

    @Test
    void updateTaskTest() {
        Task newTask = new Task("new task", "new description");

        manager.updateTask(newTask, 1);

        assertEquals(newTask, manager.getTask(1));
    }

    @Test
    void shouldNotUpdateTaskWhenNull() {
        manager.updateTask(null, 1);

        assertEquals(task1, manager.getTask(1));
    }

    @Test
    void shouldReturnListOfTasks() {
        List<Task> expected = List.of(task1, task2);

        assertIterableEquals(expected, manager.getTasks());
    }

    @Test
    void shouldReturnNullWhenNoTasks() {
        manager.clearTasks();

        assertNull(manager.getTasks());
    }

    @Test
    void removeTaskTest() {
        List<Task> expected = List.of(task2);

        manager.removeTask(1);
        assertIterableEquals(expected, manager.getTasks());
    }

    //test set for History getter
    @Test
    void shouldReturnEmptyListWhenNoElementsInHistory() {
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void shouldReturnCorrectHistoryList() {
        List<Task> expected = List.of(task2, subTask1, epic1);

        manager.getEpic(3);
        manager.getTask(2);
        manager.getEpic(3);
        manager.getSubTask(4);
        manager.getEpic(3);

        assertIterableEquals(expected, manager.getHistory());
    }
}