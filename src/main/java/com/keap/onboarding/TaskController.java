package com.keap.onboarding;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
public class TaskController {

  private TaskService taskService;

  @RequestMapping(value="contact/{contactId}/task", method=RequestMethod.GET, produces="application/json")
  public ResponseEntity getTasks(@PathVariable("contactId") String contactId) throws IOException {
    taskService = new TaskService();

    // Check if contactId is integer
    if (!taskService.isValidContactId(contactId)) {
      Map<String, Object> response = new HashMap<>();
      response.put("message", "Contact Id must be a positive integer");
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(response);
    }

    // Check if contact exists by given id
    if (!taskService.contactExists(contactId)) {
      Map<String, Object> response = new HashMap<>();
      response.put("message", "Contact doesn't exist");
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(response);
    }

    // Get list of tasks
    Map<String, Object> taskResponse = taskService.getTasks(contactId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(taskResponse.get("tasks"));
  }

  @RequestMapping(value="contact/{contactId}/task", method=RequestMethod.POST, produces="application/json", consumes="application/json")
  @ResponseBody
  public ResponseEntity createTask(@PathVariable("contactId") String contactId, @RequestBody Map<String, Object> task) throws IOException {
    taskService = new TaskService();

    Map<String, Object> createdTask;
    try {
      createdTask = taskService.createTask(contactId, task);
    } catch (HttpClientErrorException e) {
      return ResponseEntity
          .status(e.getStatusCode())
          .body(e.getResponseBodyAsString());
    }

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdTask);
  }

}
