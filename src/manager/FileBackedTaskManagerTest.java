package manager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import task.Epic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    FileBackedTaskManager createNewManager() {
        return new FileBackedTaskManager("fileBackedTaskManager_test.csv");
    }

    @AfterAll
    static void removeTestFile() {
        try {
            Files.delete(Path.of("src/manager/history/fileBackedTaskManager_test.csv"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void loadFromFileTest() {
        TaskManager loadedTest = createFromFile();

        assertEquals(manager, loadedTest);
    }

    @Test
    void loadFromFileWhenNoTasks() {
        manager.clearTasks();
        TaskManager loadedTest = createFromFile();

        assertEquals(manager, loadedTest);
    }

    @Test
    void loadFromFileWhenNoSubTasks() {
        Epic newEpic = new Epic("new epic", "new description");
        manager.addEpic(newEpic);

        TaskManager loadedTest = createFromFile();

        assertEquals(manager, loadedTest);
    }

    @Test
    void loadFromFileWithHistory() {
        manager.getTask(1);
        manager.getEpic(3);
        manager.getSubTask(4);
        manager.getTask(1);

        TaskManager loadedTest = createFromFile();

        assertEquals(manager, loadedTest);
    }

    @Test
    void shouldThrowExceptionWhenLoadingFromNotExistingFIle() {
        ManagerSaveException e = assertThrows(
                ManagerSaveException.class,
                () -> {
                    FileBackedTaskManager.loadFromFile(
                            new File("src/manager/history/COOLFILENAME.csv"));
                });
        assertTrue(e.getMessage().contains("Не удается найти указанный файл"));
    }

    private TaskManager createFromFile() {
        return FileBackedTaskManager.loadFromFile(
                new File("src/manager/history/fileBackedTaskManager_test.csv"));
    }
}