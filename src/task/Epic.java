package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private ArrayList<Integer> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        subTasks = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", subTasks.size=" + subTasks.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 31 * hash + subTasks.hashCode();
        return hash;
    }

    public void addSubTask(int subTask) {
        subTasks.add(subTask);
    }

    public void removeSubtask(int subTask) {
        if (subTasks.isEmpty()) {
            System.out.println("There are no sub tasks in this epic");
            return;
        }
        if (subTasks.remove((Integer) subTask))
            System.out.println("Sub task [ID=" + subTask + "] successfully removed");
        else
            System.out.println("There is no such a sub task to remove");
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    public void clear() {
        if (subTasks.isEmpty())
            return;
        subTasks.clear();
    }
}
