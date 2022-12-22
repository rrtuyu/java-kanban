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
        return id == epic.id;
    }

    public void addSubTask(int subTask) {
        subTasks.add(subTask);
    }

    public void removeSubtask(int subTask) {
        if (subTasks.isEmpty()) {
            System.out.println("There are no sub tasks in this epic");
            return;
        }
        if (subTasks.contains(subTask)) {
            for (int i = 0; i < subTasks.size(); i++) {
                if (subTasks.get(i) == subTask) {
                    subTasks.remove(i);
                    System.out.println("Sub task [ID=" + subTask + "] successfully removed");
                    return;
                }
            }
        } else
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
