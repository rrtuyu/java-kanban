package test;

import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    private Task task1;
    private Task task2;

    private Epic epic1;

    private SubTask subTask1;
    private SubTask subTask2;

    protected abstract T createNewManager();

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
        Epic newEpic = new Epic("new epic", "new description");

        manager.addEpic(newEpic);
        List<Epic> expected = List.of(epic1, newEpic);

        assertIterableEquals(expected, manager.getEpics());
    }

    @Test
    void shouldThrowWhenAddNullEpic() {
        String expected = "Empty object cannot be added";
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    manager.addEpic(null);
                }
        );
        assertEquals(expected, e.getMessage());
    }

    @Test
    void updateEpicTest() {
        Epic newEpic = new Epic("new epic", "new description");

        manager.updateEpic(newEpic, 3);
        assertEquals(newEpic, manager.getEpic(3));
    }

    @Test
    void shouldNotUpdateEpicWhenNull() {
        String expected = "Empty object can't be updated";
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    manager.updateEpic(null, 3);
                }
        );

        assertEquals(expected, e.getMessage());
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
    void shouldReturnIn_ProgressWhenSubsAreInDifferentStatusDone_New() {
        Status expected = Status.IN_PROGRESS;

        subTask2.setStatus(Status.DONE);
        manager.updateSubTask(subTask2, 5);

        assertEquals(expected, manager.getEpic(3).getStatus());
    }

    @Test
    void shouldReturnIn_ProgressWhenSubsAreInDifferentStatusInProgress_New() {
        Status expected = Status.IN_PROGRESS;

        subTask2.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask2, 5);

        assertEquals(expected, manager.getEpic(3).getStatus());
    }

    @Test
    void shouldReturnIn_ProgressWhenSubsAreInDifferentStatusInProgress_Done() {
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
        SubTask newSub = new SubTask("new sub", "new description");

        manager.addSubTask(newSub);
        List<SubTask> expected = List.of(subTask1, subTask2, newSub);

        assertIterableEquals(expected, manager.getSubTasks());
    }

    @Test
    void shouldThrowExWhenAddNullSub() {
        String expected = "Empty object cannot be added";
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    manager.addSubTask(null);
                }
        );
        assertEquals(expected, e.getMessage());
    }

    @Test
    void updateSubTaskTest() {
        SubTask newSub = new SubTask("new sub", "new description");

        manager.updateSubTask(newSub, 4);

        assertEquals(newSub, manager.getSubTask(4));
    }

    @Test
    void shouldNotSubTaskUpdateWhenNull() {
        String expected = "Empty object can't be updated";
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    manager.updateSubTask(null, 4);
                }
        );

        assertEquals(expected, e.getMessage());
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
    void shouldThrowExWhenParentEpicIsRemoved() {
        String expected = "Sub task list is empty";
        manager.removeEpic(3);
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    assertNull(manager.getSubTask(5));
                }
        );
        System.out.println(e.getMessage());
        assertEquals(expected, e.getMessage());
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
        Task newTask = new Task("new task", "new description");

        manager.addTask(newTask);
        List<Task> expected = List.of(task1, task2, newTask);

        assertIterableEquals(expected, manager.getTasks());
    }

    @Test
    void shouldThrowExWhenAddNullTask() {
        String expected = "Empty object cannot be added";
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    manager.addTask(null);
                }
        );
        assertEquals(expected, e.getMessage());
    }

    @Test
    void updateTaskTest() {
        Task newTask = new Task("new task", "new description");

        manager.updateTask(newTask, 1);

        assertEquals(newTask, manager.getTask(1));
    }

    @Test
    void shouldThrowExUpdateTaskWhenNull() {
        String expected = "Empty object can't be updated";
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    manager.updateTask(null, 1);
                }
        );
        assertEquals(expected, e.getMessage());
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

    //test set for time related features

    //timeline1 |----|
    //timeline2    |----|
    @Test
    void shouldNotLetCollideTasksTimeCase1() {
        Task testTask1 = new Task("asd", "asd");
        testTask1.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 10L);

        Task testTask2 = new Task("asd", "asd");
        testTask2.setDuration(LocalDateTime.of(2000, 1, 1, 0, 5), 10L);

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    manager.addTask(testTask1);
                    manager.addTask(testTask2);
                });

        assertTrue(e.getMessage().contains("Tasks' runtime should not collide."));
    }

    //timeline1    |----|
    //timeline2 |----|
    @Test
    void shouldNotLetCollideTasksTimeCase2() {
        Task testTask1 = new Task("asd", "asd");
        testTask1.setDuration(LocalDateTime.of(2000, 1, 1, 0, 5), 10L);

        Task testTask2 = new Task("asd", "asd");
        testTask2.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 10L);

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    manager.addTask(testTask1);
                    manager.addTask(testTask2);
                });

        assertTrue(e.getMessage().contains("Tasks' runtime should not collide."));
    }

    //timeline1 |----|
    //timeline2  |--|
    @Test
    void shouldNotLetCollideTasksTimeCase3() {
        Task testTask1 = new Task("asd", "asd");
        testTask1.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 10L);

        Task testTask2 = new Task("asd", "asd");
        testTask2.setDuration(LocalDateTime.of(2000, 1, 1, 0, 2), 5L);

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    manager.addTask(testTask1);
                    manager.addTask(testTask2);
                });

        assertTrue(e.getMessage().contains("Tasks' runtime should not collide."));
    }

    //timeline1  |--|
    //timeline2 |----|
    @Test
    void shouldNotLetCollideTasksTimeCase4() {
        Task testTask1 = new Task("asd", "asd");
        testTask1.setDuration(LocalDateTime.of(2000, 1, 1, 0, 2), 5L);

        Task testTask2 = new Task("asd", "asd");
        testTask2.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 10L);

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    manager.addTask(testTask1);
                    manager.addTask(testTask2);
                });

        assertTrue(e.getMessage().contains("Tasks' runtime should not collide."));
    }

    //timeline1 |----|
    //timeline2 |----|
    @Test
    void shouldNotLetCollideTasksTimeCase5() {
        Task testTask1 = new Task("asd", "asd");
        testTask1.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 10L);

        Task testTask2 = new Task("asd", "asd");
        testTask2.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 10L);

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    manager.addTask(testTask1);
                    manager.addTask(testTask2);
                });

        assertTrue(e.getMessage().contains("Tasks' runtime should not collide."));
    }

    //timeline1 |----|
    //timeline2 |--------|
    @Test
    void shouldNotLetCollideTasksTimeCase6() {
        Task testTask1 = new Task("asd", "asd");
        testTask1.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 10L);

        Task testTask2 = new Task("asd", "asd");
        testTask2.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 20L);

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    manager.addTask(testTask1);
                    manager.addTask(testTask2);
                });

        assertTrue(e.getMessage().contains("Tasks' runtime should not collide."));
    }

    //timeline1 |--------|
    //timeline2 |----|
    @Test
    void shouldNotLetCollideTasksTimeCase7() {
        Task testTask1 = new Task("asd", "asd");
        testTask1.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 20L);

        Task testTask2 = new Task("asd", "asd");
        testTask2.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 10L);

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    manager.addTask(testTask1);
                    manager.addTask(testTask2);
                });

        assertTrue(e.getMessage().contains("Tasks' runtime should not collide."));
    }

    //timeline1     |----|
    //timeline2 |--------|
    @Test
    void shouldNotLetCollideTasksTimeCase8() {
        Task testTask1 = new Task("asd", "asd");
        testTask1.setDuration(LocalDateTime.of(2000, 1, 1, 0, 10), 10L);

        Task testTask2 = new Task("asd", "asd");
        testTask2.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 20L);

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    manager.addTask(testTask1);
                    manager.addTask(testTask2);
                });

        assertTrue(e.getMessage().contains("Tasks' runtime should not collide."));
    }

    //timeline1 |--------|
    //timeline2     |----|
    @Test
    void shouldNotLetCollideTasksTimeCase9() {
        Task testTask1 = new Task("asd", "asd");
        testTask1.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 20L);

        Task testTask2 = new Task("asd", "asd");
        testTask2.setDuration(LocalDateTime.of(2000, 1, 1, 0, 10), 10L);

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    manager.addTask(testTask1);
                    manager.addTask(testTask2);
                });

        assertTrue(e.getMessage().contains("Tasks' runtime should not collide."));
    }

    //timeline1 |----|
    //timeline2      |----|
    @Test
    void shouldNotLetCollideTasksTimeCase10() {
        Task testTask1 = new Task("asd", "asd");
        testTask1.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 10L);

        Task testTask2 = new Task("asd", "asd");
        testTask2.setDuration(LocalDateTime.of(2000, 1, 1, 0, 10), 10L);

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    manager.addTask(testTask1);
                    manager.addTask(testTask2);
                });

        assertTrue(e.getMessage().contains("Tasks' runtime should not collide."));
    }

    //timeline1      |----|
    //timeline2 |----|
    @Test
    void shouldNotLetCollideTasksTimeCase11() {
        Task testTask1 = new Task("asd", "asd");
        testTask1.setDuration(LocalDateTime.of(2000, 1, 1, 0, 10), 10L);

        Task testTask2 = new Task("asd", "asd");
        testTask2.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 10L);

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    manager.addTask(testTask1);
                    manager.addTask(testTask2);
                });

        assertTrue(e.getMessage().contains("Tasks' runtime should not collide."));
    }

    //timeline1 |----|
    //timeline2       |----|
    @Test
    void addTaskWithTimeTest1() {
        Task testTask1 = new Task("asd", "asd");
        testTask1.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 10L);

        Task testTask2 = new Task("asd", "asd");
        testTask2.setDuration(LocalDateTime.of(2000, 1, 1, 0, 11), 10L);

        assertDoesNotThrow(
                () -> {
                    manager.addTask(testTask1);
                    manager.addTask(testTask2);
                });
    }

    //timeline1       |----|
    //timeline2 |----|
    @Test
    void addTaskWithTimeTest2() {
        Task testTask1 = new Task("asd", "asd");
        testTask1.setDuration(LocalDateTime.of(2000, 1, 1, 0, 11), 10L);

        Task testTask2 = new Task("asd", "asd");
        testTask2.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 10L);

        assertDoesNotThrow(
                () -> {
                    manager.addTask(testTask1);
                    manager.addTask(testTask2);
                });
    }

    @Test
    void getPrioritizedTasksTest() {
        subTask1.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 10L);
        subTask2.setDuration(LocalDateTime.of(2000, 1, 1, 0, 20), 10L);
        task1.setDuration(LocalDateTime.of(1999, 12, 31, 23, 40), 10L);

        manager.updateTask(task1, task1.getId());
        manager.updateSubTask(subTask1, subTask1.getId());
        manager.updateSubTask(subTask2, subTask2.getId());

        Iterator<Task> expected = manager.getPrioritizedTasks().iterator();

        assertEquals(task1, expected.next());
        assertEquals(subTask1, expected.next());
        assertEquals(subTask2, expected.next());
        assertNull(expected.next().getStartTime());
    }

    @Test
    void getPriorityWhenIllegalTaskAdded() {
        SubTask testSub1 = new SubTask("test1", "test1");
        testSub1.setDuration(LocalDateTime.of(2000, 1, 1, 0, 0), 10L);
        manager.addSubTask(testSub1);

        SubTask testSub2 = new SubTask("test2", "test2");
        testSub2.setDuration(LocalDateTime.of(2000, 1, 1, 0, 5), 10L);
        try {
            manager.addSubTask(testSub2); //adding colliding task
        } catch (IllegalArgumentException e) {
        }

        assertTrue(manager.getPrioritizedTasks().contains(testSub1));
        assertFalse(manager.getPrioritizedTasks().contains(testSub2));
    }
}