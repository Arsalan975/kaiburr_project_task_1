package com.example.taskrunner.service;

import com.example.taskrunner.model.Task;
import com.example.taskrunner.model.TaskExecution;
import com.example.taskrunner.repo.TaskRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
public class TaskService {
  private final TaskRepository repo;
  private final CommandValidator validator;
  private final TaskRunner runner;

  public TaskService(TaskRepository repo, CommandValidator validator, TaskRunner runner) {
    this.repo = repo; this.validator = validator; this.runner = runner;
  }

  public List<Task> getAll() { return repo.findAll(); }

  public Task getByIdOrThrow(String id) {
    return repo.findById(id).orElseThrow(() -> new NotFound("Task not found: " + id));
  }

  public Task upsert(Task incoming) {
    validator.validateOrThrow(incoming.getCommand());
    return repo.save(incoming);
  }

  public void delete(String id) {
    if (!repo.existsById(id)) throw new NotFound("Task not found: " + id);
    repo.deleteById(id);
  }

  public List<Task> findByNameContains(String q) {
    var list = repo.findByNameContainingIgnoreCase(q);
    if (list.isEmpty()) throw new NotFound("No tasks found for query: " + q);
    return list;
  }

  public TaskExecution executeAndStore(String id) throws Exception {
    Task task = getByIdOrThrow(id);
    validator.validateOrThrow(task.getCommand());
    TaskExecution exec = new TaskExecution();
    exec.setStartTime(Instant.now());
    String output = runner.run(task.getCommand());
    exec.setEndTime(Instant.now());
    exec.setOutput(output);
    task.getTaskExecutions().add(exec);
    repo.save(task);
    return exec;
  }

  public static class NotFound extends RuntimeException { public NotFound(String m){ super(m);} }
}
