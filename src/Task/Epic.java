package Task;

import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<SubTask> subTasks;

    public Epic(String name, String description){
        super(name, description);
        subTasks = new ArrayList<>();
        status = statusNew;
    }

    @Override
    public String toString() {

        return "Epic{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", subTasks.size=" +  subTasks.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return id == epic.id;
    }

    public void updateStatus(){
        int doneCounter = 0;
        switch (status){

            case statusNew:
                if (subTasks.isEmpty())
                    return;
                for (SubTask subTask : subTasks) {
                    if (subTask.getStatus().equals(statusInProgress)) {
                        status = statusInProgress;
                        break;
                    }
                    if (subTask.getStatus().equals(statusDone))
                        doneCounter++;
                }
                if (doneCounter == subTasks.size())
                    status = statusDone;
                else if (doneCounter > 0)
                    status = statusInProgress;
                break;

            case statusInProgress:
                if (subTasks.isEmpty()){
                    status = statusNew;
                    return;
                }
                for (SubTask subTask : subTasks) {
                    if (subTask.getStatus().equals(statusDone))
                        doneCounter++;
                }
                if (doneCounter == subTasks.size())
                    status = statusDone;
                break;

            case statusDone:
                if (subTasks.isEmpty()){
                    status = statusNew;
                    return;
                }
                int newCounter = 0;
                for (SubTask subTask : subTasks) {
                    if (subTask.getStatus().equals(statusInProgress)){
                        status = statusInProgress;
                        return;
                    }
                    if(subTask.getStatus().equals(statusNew))
                        newCounter++;
                }
                if (newCounter == subTasks.size())
                    status = statusNew;
                else if (newCounter > 0)
                    status = statusInProgress;
                break;

            default:
                System.out.println("Something went wrong, unrecognizable status!\nPlease check your status related methods/fields.");
                break;
        }
    }

    public void addSubTask(SubTask subTask){
        subTasks.add(subTask);
        subTask.linkToEpic(this);
    }

    public void removeSubtask(SubTask subTask){
        if (subTasks.isEmpty()){
            System.out.println("There are no sub tasks in this epic");
            return;
        }
        if(subTasks.contains(subTask)) {
            subTasks.remove(subTask);
            subTask.removeParentEpic();
        } else
            System.out.println("There is no such a sub task to remove");
    }

    public ArrayList<Integer> getSubTasks(){
        if (subTasks.isEmpty())
            return null;
        ArrayList<Integer> subTasksId = new ArrayList<>();
        for (SubTask subTask : subTasks) {
            subTasksId.add(subTask.getId());
        }
        return subTasksId;
    }

    public void clear(){
        if (subTasks.isEmpty())
            return;
        for (SubTask subTask : subTasks) {
            subTask.removeParentEpic();
        }
        subTasks.clear();
    }
}
