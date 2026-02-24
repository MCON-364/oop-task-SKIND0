package edu.touro.las.mcon364.taskmanager;

//Part 2A — new command that changes a task's priority

public final class ChangePriorityCommand implements Command {
    private final TaskRegistry registry;
    private final String taskName;
    private final Priority newPriority;

    public ChangePriorityCommand(TaskRegistry registry, String taskName, Priority newPriority) {
        this.registry = registry;
        this.taskName = taskName;
        this.newPriority = newPriority;
    }

    @Override
    public void execute() {
        Task existing = registry.get(taskName)
                .orElseThrow(() -> new TaskNotFoundException(taskName));
        registry.remove(taskName);
        registry.add(new Task(existing.name(), newPriority));
    }
}