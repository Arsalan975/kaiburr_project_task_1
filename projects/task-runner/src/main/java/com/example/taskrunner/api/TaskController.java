package com.example.taskrunner.api;

import com.example.taskrunner.model.Task;
import com.example.taskrunner.model.TaskExecution;
import com.example.taskrunner.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TaskController {
  private final TaskService svc;
  public TaskController(TaskService svc) { this.svc = svc; }

 
  @GetMapping("/tasks")
  public Object getTasks(@RequestParam(name = "id", required = false) String id) {
    if (id == null || id.isBlank()) return svc.getAll();
    return svc.getByIdOrThrow(id);
  }

  @PutMapping("/tasks")
  @ResponseStatus(HttpStatus.OK)
  public Task putTask(@Valid @RequestBody Task task) { return svc.upsert(task); }

  @DeleteMapping("/tasks/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTask(@PathVariable String id) { svc.delete(id); }

  @GetMapping("/tasks/search")
  public List<Task> searchByName(@RequestParam("query") String query) {
    return svc.findByNameContains(query);
  }

  @PutMapping("/tasks/{id}/executions")
  @ResponseStatus(HttpStatus.CREATED)
  public TaskExecution execute(@PathVariable String id) throws Exception {
    return svc.executeAndStore(id);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(TaskService.NotFound.class)
  public Map<String,String> nf(TaskService.NotFound ex){ return Map.of("error", ex.getMessage()); }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalArgumentException.class)
  public Map<String,String> br(IllegalArgumentException ex){ return Map.of("error", ex.getMessage()); }
}
