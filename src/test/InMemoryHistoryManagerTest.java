package test;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;

    @BeforeEach
    void taskSetInit() {
        historyManager = new InMemoryHistoryManager();

        task1 = new Task("test task 1", "test task description 1");
        task1.setId(1);
        task2 = new Task("test task 2", "test task description 2");
        task2.setId(2);

        epic1 = new Epic("test epic 1", "test epic description 1");
        epic1.setId(3);
        epic2 = new Epic("test epic 2", "test epic description 2");
        epic2.setId(4);

        subTask1 = new SubTask("test subtask 1", "test subtask description 1");
        subTask1.setId(5);
        subTask2 = new SubTask("test subtask 2", "test subtask description 2");
        subTask2.setId(6);
    }

    @Test
    void shouldReturnEmptyListWhenNoElementsAdded() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldReturnCorrectHistoryWhenHasDuplicates() {
        List<Task> expected = List.of(task1, epic2, subTask1, subTask2, task2, epic1);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.add(epic2);
        historyManager.add(subTask1);
        historyManager.add(subTask2);
        historyManager.add(task2);
        historyManager.add(epic1);

        assertIterableEquals(expected, historyManager.getHistory());
    }

    @Test
    void shouldReturnCorrectHistoryWithoutDuplicates() {
        List<Task> expected = List.of(task1, epic1, subTask1, subTask2, epic2, task2);

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subTask1);
        historyManager.add(subTask2);
        historyManager.add(epic2);
        historyManager.add(task2);

        assertIterableEquals(expected, historyManager.getHistory());
    }

    @Test
    void shouldReturnCorrectHistoryWhenElementRemovedFromTheStart() {
        List<Task> expected = List.of(subTask1, subTask2, epic2, task2);

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subTask1);
        historyManager.add(subTask2);
        historyManager.add(epic2);
        historyManager.add(task2);

        historyManager.remove(1);
        historyManager.remove(3);

        assertIterableEquals(expected, historyManager.getHistory());
    }

    @Test
    void shouldReturnCorrectHistoryWhenElementRemovedFromTheMiddle() {
        List<Task> expected = List.of(task1, epic1, epic2, task2);

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subTask1);
        historyManager.add(subTask2);
        historyManager.add(epic2);
        historyManager.add(task2);

        historyManager.remove(5);
        historyManager.remove(6);

        assertIterableEquals(expected, historyManager.getHistory());
    }

    @Test
    void shouldReturnCorrectHistoryWhenElementRemovedFromTheEnd() {
        List<Task> expected = List.of(task1, epic1, subTask1, subTask2);

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subTask1);
        historyManager.add(subTask2);
        historyManager.add(epic2);
        historyManager.add(task2);

        historyManager.remove(2);
        historyManager.remove(4);

        assertIterableEquals(expected, historyManager.getHistory());
    }
}