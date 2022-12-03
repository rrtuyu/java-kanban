package Task;

public class SubTask extends Task{
    private Epic parentEpic;

    public SubTask(String name, String description, Epic parentEpic){
        super(name, description);
        this.parentEpic = parentEpic;
    }

    @Override
    public String toString() {
        String parentEpicId;
        if (parentEpic == null)
            parentEpicId = "null";
        else
            parentEpicId = String.valueOf(parentEpic.getId());
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
        return id == subTask.id && parentEpic.id == subTask.parentEpic.id;
    }

    @Override
    public void updateStatus(String status){
        if (!(status.equals(statusNew) || status.equals(statusInProgress) || status.equals(statusDone))){
            System.out.println("Unrecognizable status, use other value");
            return;
        }
        this.status = status;
        parentEpic.updateStatus();
    }

    public void linkToEpic(Epic epic){
        if(parentEpic != null && !epic.equals(parentEpic))
            parentEpic.removeSubtask(this);
        if(epic != null) {
            epic.updateStatus();
        }
        parentEpic = epic;
    }

    public Epic getParentEpic(){
        return parentEpic;
    }

    public void removeParentEpic(){
        parentEpic = null;
    }
}
