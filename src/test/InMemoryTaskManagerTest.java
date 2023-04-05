package test;

import manager.Managers;
import manager.TaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @Override
    protected TaskManager createNewManager() {
        return Managers.getInMemoryManager();
    }
}