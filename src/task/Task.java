package task;

public class Task {
    protected int id;
    protected boolean hasId;
    protected Status status;
    protected String name;
    protected String description;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status = Status.NEW;
        hasId = false;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        Task otherTask = (Task) o;

        return hashCode() == otherTask.hashCode();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int hash = 17;
        hash = prime * hash + name.hashCode();
        hash = prime * hash + description.hashCode();
        hash = prime * hash + id;
        hash = prime * hash + status.hashCode();
        hash = prime * hash + getClass().hashCode();
        return hash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        hasId = true;
    }

    public boolean hasId() {
        return hasId;
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

    public Type getType() {
        if (this instanceof SubTask) return Type.SUBTASK;
        if (this instanceof Epic) return Type.EPIC;
        return Type.TASK;
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
