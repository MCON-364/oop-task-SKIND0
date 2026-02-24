# MCON 364 – Modern Java OOP Refactoring

This GitHub Classroom assignment focuses on refactoring an existing Java OOP system
using **modern Java features (Java 8–21)**.

---

## Part 1: In-Class Exercise (45 minutes)

You are provided with an existing task-management system that uses object-oriented design and Java collections. Your goal is to refactor this system using modern Java features **without changing its behavior**.

### Step 1: Convert a Data Class to a Record
- Replace the existing `Task` class with a `record`
- Remove unnecessary boilerplate
- Ensure the program still compiles and runs

### Step 2: Seal the Command Hierarchy
- Make the `Command` interface sealed
- Explicitly list the permitted implementations
- Discuss how this improves safety and clarity

### Step 3: Refactor TaskManager.run Using Pattern-Matching Switch
- Replace the `instanceof` chain in `TaskManager.run()` with a pattern-matching `switch`
- Use JDK 21+ pattern-matching features for cleaner, type-safe code
- Ensure the behavior remains unchanged
- In TaskManager javadoc comments discuss how this improves readability and maintainability

### Step 4: Improve Exception Handling
This step focuses on replacing old-style null checks with modern `Optional` and custom exceptions.

**4.1 Create a Custom Exception**
- Create `TaskNotFoundException.java` in the taskmanager package
- Make it extend `RuntimeException` (or `Exception` if you prefer checked exceptions)
- Add a constructor that accepts a descriptive message

**4.2 Refactor `TaskRegistry.get()`**
- Change the return type from `Task` to `Optional<Task>`
- Wrap the result using `Optional.ofNullable()`
- This prevents callers from receiving null values

**4.3 Refactor `UpdateTaskCommand.execute()`**
- Replace the `if (existing == null)` check with `Optional.orElseThrow()`
- Throw your custom `TaskNotFoundException` when the task doesn't exist
- Remove the silent failure and `System.err.println()` warning
- Example:
```java
Task existing = registry.get(taskName)
    .orElseThrow(() -> new TaskNotFoundException("Task '" + taskName + "' not found"));
```

**4.4 Update Any Other Affected Code**
- Fix any other places that call `TaskRegistry.get()` to handle the new `Optional<Task>` return type
- Ensure all tests still pass after these changes


### Step 5: Run and Reflect
- Run the demo program
- Verify behavior is unchanged
- Discuss what became simpler or safer

---
[TEST_README.md](TEST_README.md)
## Part 2: Homework Assignment

### Part A: Extend the Command System
- Add a new command (for example, changing task priority)
- Ensure it fits into the sealed hierarchy

### Part B: Exception Design
- Add at least one new custom exception
- Use `Optional` instead of returning null

### Part C: Collections Practice
Add a method that groups tasks by priority:
```java
Map<Priority, List<Task>> getTasksByPriority();
```
This method should use collections and modern Java features cleanly and safely.

---

## Submission Requirements
- All tests must pass
- Code must compile without errors
- Behavior of the original system must be preserved
