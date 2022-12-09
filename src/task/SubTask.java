package task;

public class SubTask extends Task {
    private Integer parentEpic;

    public SubTask(String name, String description) {
        super(name, description);
    }

    @Override
    public String toString() {
        String parentEpicId;
        if (parentEpic == null)
            parentEpicId = "null";
        else
            parentEpicId = String.valueOf(parentEpic);
        return "SubTask{" +
                "parentEpicId=" + parentEpicId +
                ", id=" + id +
                ", status='" + status + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subTask = (SubTask) o;
        return id == subTask.id && parentEpic == subTask.parentEpic;
    }

    @Override
    public void setStatus(String status) {
        if (!(status.equals(statusNew) || status.equals(statusInProgress) || status.equals(statusDone))) {
            System.out.println("Unrecognizable status, use other value");
            return;
        }
        this.status = status;
    }

    public void linkToEpic(int epic) {
        parentEpic = epic;
    }

    public Integer getParentEpic() {
        return parentEpic;
    }

    public void removeParentEpic() {
        parentEpic = null;
    }
}
