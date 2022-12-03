package Task;

public class Task{
    private static int globalCount = 0;
    protected int id;
    protected String status;
    protected String name;
    protected String description;
    //константы имен статусов
    public static final String statusNew = "NEW";
    public static final String statusInProgress = "IN_PROGRESS";
    public static final String statusDone = "DONE";

    public Task(String name, String description){
        this.name = name;
        this.description = description;
        status = statusNew;
        id = generateId();
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected int generateId(){
        return ++globalCount;
    }

    public void updateStatus(String status){
        if (!(status.equals(statusNew) || status.equals(statusInProgress) || status.equals(statusDone))){
            System.out.println("Unrecognizable status, use other value");
            return;
        }
        this.status = status;
    }
}
