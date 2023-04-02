package manager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    InMemoryTaskManager createNewManager() {
        return new InMemoryTaskManager();
    }
}