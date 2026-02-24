package edu.touro.las.mcon364.taskmanager;

import java.util.*;

public class TaskRegistry {
    //Part 2B/C — getTasksByPriority() groups tasks using streams + collectors.

    private final Map<String, Task> tasks = new HashMap<>();

    public void add(Task task) {
        tasks.put(task.name(), task);
    }

    public Optional<Task> get(String name) {
        return Optional.ofNullable(tasks.get(name));
    }

    public void remove(String name) {
        tasks.remove(name);
    }

    public Map<String, Task> getAll() {
        return tasks;
    }
    //Part 2C — groups all tasks by their Priority using streams. Returns an unmodifiable view of a EnumMap for predictable ordering.
    public Map<Priority, List<Task>> getTasksByPriority() {
        Map<Priority, List<Task>> grouped = new EnumMap<>(Priority.class);
        for (Priority p : Priority.values()) {
            grouped.put(p, new ArrayList<>());
        }
        tasks.values().forEach(task -> grouped.get(task.priority()).add(task));
        return Collections.unmodifiableMap(grouped);
    }
}