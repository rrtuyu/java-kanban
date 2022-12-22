package task;

public class Task {
    protected int id;
    protected Status status;
    protected String name;
    protected String description;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status = Status.NEW;
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

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        if (!(status.equals(Status.NEW) || status.equals(Status.IN_PROGRESS) || status.equals(Status.DONE))) {
            System.out.println("Unrecognizable status, use other value");
            return;
        }
        this.status = status;
    }
}
