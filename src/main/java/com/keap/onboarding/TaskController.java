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
  public ResponseEntity getTasks(@PathVariable("contactId") String contactId, String accessToken) throws IOException {

    taskService = new TaskService(accessToken);

    // Check if accessToken was provided
    if (!taskService.hasValidAccessToken()) {
      Map<String, Object> response = new HashMap<>();
      response.put("message", "401 Unauthorized");
      return ResponseEntity
              .status(HttpStatus.UNAUTHORIZED)
              .body(response);
    }

    // Check if contactId is integer
    if (!taskService.isValidContactId(contactId)) {
      Map<String, Object> response = new HashMap<>();
      response.put("message", "Contact Id must be a positive integer");
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(response);
    }

    // Check if contact exists by given id
    ResponseEntity contactExistsResponse = taskService.contactExists(contactId);
    if (contactExistsResponse.getStatusCode() != HttpStatus.OK) {
      Map<String, Object> response = new HashMap<>();
      if (contactExistsResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
        response.put("message", "Contact doesn't exist");
      } else {
        response.put("message", contactExistsResponse.getBody());
      }
      return ResponseEntity
          .status(contactExistsResponse.getStatusCode())
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
  public ResponseEntity createTask(@PathVariable("contactId") String contactId, @RequestBody Map<String, Object> task, String accessToken) throws IOException {
    taskService = new TaskService(accessToken);

    // Check if accessToken was provided
    if (!taskService.hasValidAccessToken()) {
      Map<String, Object> response = new HashMap<>();
      response.put("message", "401 Unauthorized");
      return ResponseEntity
              .status(HttpStatus.UNAUTHORIZED)
              .body(response);
    }

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
