package edu.touro.las.mcon364.taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DemoMainTest {
    private DemoMain demo;
    private TaskRegistry registry;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        demo = new DemoMain();
        registry = new TaskRegistry();

        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @DisplayName("Adding tasks should create 5 tasks with correct priorities")
    void testDemonstrateAddingTasks() {
        TaskRegistry testRegistry = new TaskRegistry();
        TaskManager testManager = new TaskManager(testRegistry);

        testManager.run(new AddTaskCommand(testRegistry, new Task("Write documentation", Priority.HIGH)));
        testManager.run(new AddTaskCommand(testRegistry, new Task("Review pull requests", Priority.MEDIUM)));
        testManager.run(new AddTaskCommand(testRegistry, new Task("Update dependencies", Priority.LOW)));
        testManager.run(new AddTaskCommand(testRegistry, new Task("Fix critical bug", Priority.HIGH)));
        testManager.run(new AddTaskCommand(testRegistry, new Task("Refactor code", Priority.MEDIUM)));

        assertEquals(5, testRegistry.getAll().size(), "Should have 5 tasks");

        assertEquals(Priority.HIGH,   testRegistry.get("Write documentation").get().priority(),  "Write documentation should be HIGH priority");
        assertEquals(Priority.MEDIUM, testRegistry.get("Review pull requests").get().priority(), "Review pull requests should be MEDIUM priority");
        assertEquals(Priority.LOW,    testRegistry.get("Update dependencies").get().priority(),  "Update dependencies should be LOW priority");
        assertEquals(Priority.HIGH,   testRegistry.get("Fix critical bug").get().priority(),     "Fix critical bug should be HIGH priority");
        assertEquals(Priority.MEDIUM, testRegistry.get("Refactor code").get().priority(),        "Refactor code should be MEDIUM priority");
    }

    @Test
    @DisplayName("Retrieving existing task should return correct task")
    void testDemonstrateRetrievingTask() {
        TaskRegistry testRegistry = new TaskRegistry();
        TaskManager testManager = new TaskManager(testRegistry);

        Task expectedTask = new Task("Fix critical bug", Priority.HIGH);
        testManager.run(new AddTaskCommand(testRegistry, expectedTask));

        Optional<Task> retrieved = testRegistry.get("Fix critical bug");

        assertTrue(retrieved.isPresent(), "Retrieved task should be present");
        assertEquals("Fix critical bug", retrieved.get().name(), "Task name should match");
        assertEquals(Priority.HIGH, retrieved.get().priority(), "Task priority should match");
        assertEquals(expectedTask, retrieved.get(), "Retrieved task should equal the added task");
    }

    @Test
    @DisplayName("Retrieving non-existent task should return Optional.empty()")
    void testDemonstrateRetrievingNonExistentTask() {
        TaskRegistry testRegistry = new TaskRegistry();
        Optional<Task> missing = testRegistry.get("Non-existent task");
        assertTrue(missing.isEmpty(), "Non-existent task should return Optional.empty()");
    }

    @Test
    @DisplayName("Updating task should change priority")
    void testDemonstrateUpdatingTask() {
        TaskRegistry testRegistry = new TaskRegistry();
        TaskManager testManager = new TaskManager(testRegistry);

        testManager.run(new AddTaskCommand(testRegistry, new Task("Refactor code", Priority.MEDIUM)));

        Optional<Task> before = testRegistry.get("Refactor code");
        assertEquals(Priority.MEDIUM, before.get().priority(), "Initial priority should be MEDIUM");

        testManager.run(new UpdateTaskCommand(testRegistry, "Refactor code", Priority.HIGH));

        Optional<Task> after = testRegistry.get("Refactor code");
        assertTrue(after.isPresent(), "Task should still exist after update");
        assertEquals(Priority.HIGH, after.get().priority(), "Priority should be updated to HIGH");
        assertEquals("Refactor code", after.get().name(), "Task name should remain unchanged");
    }

    @Test
    @DisplayName("Updating non-existent task should throw TaskNotFoundException")
    void testDemonstrateUpdatingNonExistentTask() {
        TaskRegistry testRegistry = new TaskRegistry();
        TaskManager testManager = new TaskManager(testRegistry);

        assertThrows(TaskNotFoundException.class, () -> {
            testManager.run(new UpdateTaskCommand(testRegistry, "Non-existent task", Priority.HIGH));
        }, "Updating non-existent task should throw TaskNotFoundException");

        assertTrue(testRegistry.get("Non-existent task").isEmpty(), "Non-existent task should not be created");
    }

    @Test
    @DisplayName("Removing task should delete it from registry")
    void testDemonstrateRemovingTask() {
        TaskRegistry testRegistry = new TaskRegistry();
        TaskManager testManager = new TaskManager(testRegistry);

        testManager.run(new AddTaskCommand(testRegistry, new Task("Update dependencies", Priority.LOW)));
        testManager.run(new AddTaskCommand(testRegistry, new Task("Fix critical bug", Priority.HIGH)));

        assertEquals(2, testRegistry.getAll().size(), "Should have 2 tasks initially");
        assertTrue(testRegistry.get("Update dependencies").isPresent(), "Update dependencies should exist");

        testManager.run(new RemoveTaskCommand(testRegistry, "Update dependencies"));

        assertEquals(1, testRegistry.getAll().size(), "Should have 1 task after removal");
        assertTrue(testRegistry.get("Update dependencies").isEmpty(), "Update dependencies should be removed");
        assertTrue(testRegistry.get("Fix critical bug").isPresent(), "Fix critical bug should still exist");
    }

    @Test
    @DisplayName("Null return demonstration - registry.get() returns Optional.empty() for missing tasks")
    void testDemonstrateNullReturn() {
        TaskRegistry testRegistry = new TaskRegistry();
        Optional<Task> missing = testRegistry.get("Non-existent task");
        assertTrue(missing.isEmpty(), "Getting non-existent task should return Optional.empty()");
    }

    @Test
    @DisplayName("Full demo run should execute without exceptions")
    void testFullDemoRun() {
        DemoMain testDemo = new DemoMain();
        assertDoesNotThrow(() -> {
            testDemo.run();
        }, "Full demo should run without exceptions");
    }

    @Test
    @DisplayName("Task equality should work correctly")
    void testTaskEquality() {
        Task task1 = new Task("Test task", Priority.HIGH);
        Task task2 = new Task("Test task", Priority.HIGH);
        Task task3 = new Task("Test task", Priority.LOW);
        Task task4 = new Task("Different task", Priority.HIGH);

        assertEquals(task1, task2, "Tasks with same name and priority should be equal");
        assertEquals(task1.hashCode(), task2.hashCode(), "Equal tasks should have same hashCode");
        assertNotEquals(task1, task3, "Tasks with different priorities should not be equal");
        assertNotEquals(task1, task4, "Tasks with different names should not be equal");
    }

    @Test
    @DisplayName("Command pattern - AddTaskCommand should execute correctly")
    void testAddTaskCommand() {
        TaskRegistry testRegistry = new TaskRegistry();
        Task task = new Task("Test task", Priority.MEDIUM);
        AddTaskCommand command = new AddTaskCommand(testRegistry, task);
        command.execute();

        assertTrue(testRegistry.get("Test task").isPresent(), "Task should be added after command execution");
        assertEquals(task, testRegistry.get("Test task").get(), "Added task should match original");
    }

    @Test
    @DisplayName("Command pattern - RemoveTaskCommand should execute correctly")
    void testRemoveTaskCommand() {
        TaskRegistry testRegistry = new TaskRegistry();
        testRegistry.add(new Task("Test task", Priority.MEDIUM));
        RemoveTaskCommand command = new RemoveTaskCommand(testRegistry, "Test task");
        command.execute();

        assertTrue(testRegistry.get("Test task").isEmpty(), "Task should be removed after command execution");
    }

    @Test
    @DisplayName("Command pattern - UpdateTaskCommand should execute correctly")
    void testUpdateTaskCommand() {
        TaskRegistry testRegistry = new TaskRegistry();
        testRegistry.add(new Task("Test task", Priority.LOW));
        UpdateTaskCommand command = new UpdateTaskCommand(testRegistry, "Test task", Priority.HIGH);
        command.execute();

        Optional<Task> updated = testRegistry.get("Test task");
        assertTrue(updated.isPresent(), "Task should still exist after update");
        assertEquals(Priority.HIGH, updated.get().priority(), "Priority should be updated");
    }

    @Test
    @DisplayName("TaskManager.run() should handle AddTaskCommand")
    void testTaskManagerRunWithAddCommand() {
        TaskRegistry testRegistry = new TaskRegistry();
        TaskManager manager = new TaskManager(testRegistry);
        Task task = new Task("Test task", Priority.HIGH);
        manager.run(new AddTaskCommand(testRegistry, task));

        assertTrue(testRegistry.get("Test task").isPresent(), "Task should be added via TaskManager.run()");
    }

    @Test
    @DisplayName("TaskManager.run() should handle RemoveTaskCommand")
    void testTaskManagerRunWithRemoveCommand() {
        TaskRegistry testRegistry = new TaskRegistry();
        TaskManager manager = new TaskManager(testRegistry);
        testRegistry.add(new Task("Test task", Priority.HIGH));
        manager.run(new RemoveTaskCommand(testRegistry, "Test task"));

        assertTrue(testRegistry.get("Test task").isEmpty(), "Task should be removed via TaskManager.run()");
    }

    @Test
    @DisplayName("TaskManager.run() should handle UpdateTaskCommand")
    void testTaskManagerRunWithUpdateCommand() {
        TaskRegistry testRegistry = new TaskRegistry();
        TaskManager manager = new TaskManager(testRegistry);
        testRegistry.add(new Task("Test task", Priority.LOW));
        manager.run(new UpdateTaskCommand(testRegistry, "Test task", Priority.HIGH));

        Optional<Task> updated = testRegistry.get("Test task");
        assertEquals(Priority.HIGH, updated.get().priority(), "Priority should be updated via TaskManager.run()");
    }
}