package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    protected int id;
    DateTimeFormatter formatter;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected boolean hasId;
    protected Status status;
    protected String name;
    protected String description;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status = Status.NEW;
        hasId = false;
        formatter = DateTimeFormatter.ofPattern("dd.MM.YYYY | hh:mm");
    }

    public void setDuration(LocalDateTime startTime, long durationInMinutes) throws IllegalArgumentException {
        if (durationInMinutes <= 0)
            throw new IllegalArgumentException("Duration must be more than 0. Provided: " + duration);
        if (startTime == null)
            throw new IllegalArgumentException("Provided start time can't be null");

        this.duration = Duration.ofMinutes(durationInMinutes);
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() throws IllegalArgumentException {
        if (startTime != null || duration != null)
            return startTime.plusMinutes(duration.toMinutes());
        else
            throw new IllegalArgumentException("Start time and duration must be initialized to get end time");
    }

    @Override
    public String toString() {
        String start;
        String end;

        if (startTime == null)
            start = "not set";
        else
            start = startTime.format(formatter);

        if (duration == null)
            end = "not set";
        else
            end = startTime.plusMinutes(duration.toMinutes()).format(formatter);

        return "Task{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
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
