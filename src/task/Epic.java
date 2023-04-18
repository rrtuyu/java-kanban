package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private ArrayList<Integer> subTasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        subTasks = new ArrayList<>();
        startTime = LocalDateTime.MAX;
        endTime = LocalDateTime.MIN;
    }

    @Override
    public String toString() {
        String start;
        String end;

        if (startTime.isEqual(LocalDateTime.MAX))
            start = "not set";
        else
            start = startTime.format(FORMATTER);

        if (endTime.isEqual(LocalDateTime.MIN))
            end = "not set";
        else
            end = endTime.format(FORMATTER);

        return "Epic{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
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

    @Override
    public void setDuration(LocalDateTime startTime, long durationInMinutes) throws IllegalArgumentException {
        if (durationInMinutes <= 0)
            throw new IllegalArgumentException("Duration must be more than 0. Provided: " + duration);
        if (startTime == null)
            throw new IllegalArgumentException("Provided start time can't be null");

        if (startTime.isBefore(this.startTime))
            this.startTime = startTime;

        LocalDateTime localDeadLine = startTime.plusMinutes(durationInMinutes);

        if (localDeadLine.isAfter(this.endTime))
            this.endTime = localDeadLine;

        duration = Duration.between(this.startTime, this.endTime);
    }

    @Override
    public LocalDateTime getStartTime() {
        if (startTime != null && startTime.isEqual(LocalDateTime.MAX))
            return null;

        return startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        if (endTime != null && endTime.isEqual(LocalDateTime.MIN))
            return null;

        return endTime;
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

    public void resetTimings() {
        startTime = LocalDateTime.MAX;
        endTime = LocalDateTime.MIN;
    }
}
