package task;

public class SubTask extends Task {
    private Integer parentEpic;

    public SubTask(String name, String description) {
        super(name, description);
    }

    @Override
    public String toString() {
        String parentEpicId;
        String start;
        String end;

        if (parentEpic == null)
            parentEpicId = "null";
        else
            parentEpicId = String.valueOf(parentEpic);

        if (startTime == null)
            start = "not set";
        else
            start = startTime.format(FORMATTER);

        if (duration == null)
            end = "not set";
        else
            end = startTime.plusMinutes(duration.toMinutes()).format(FORMATTER);

        return "SubTask{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", parentEpicId=" + parentEpicId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subTask = (SubTask) o;
        return hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if (parentEpic != null)
            hash = 31 * hash + parentEpic;
        return hash;
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
