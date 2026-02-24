package edu.touro.las.mcon364.taskmanager;

public sealed interface Command permits AddTaskCommand, ChangePriorityCommand, RemoveTaskCommand, UpdateTaskCommand {
    void execute();
}
