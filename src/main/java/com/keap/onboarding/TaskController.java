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

  private final String apiBaseURL = "https://api.infusionsoft.com/crm/rest/v1";
  private final String accessToken = "vrqckgfbdfpb38avc9vzetxf";

  @RequestMapping(value="contact/{contactId}/task", method=RequestMethod.GET, produces="application/json")
  public ResponseEntity getTasks(@PathVariable("contactId") String contactId) throws IOException {

    // Check if contactId is integer
    if (!TaskService.isValidContactId(contactId)) {
      Map<String, Object> response = new HashMap<>();
      response.put("message", "Contact Id must be a positive integer");
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(response);
    };

    // Check if contact exists by given id
    if (!TaskService.contactExists(contactId)) {
      Map<String, Object> response = new HashMap<>();
      response.put("message", "Contact doesn't exist");
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(response);
    }

    // Get list of tasks
    Map<String, Object> taskResponse = TaskService.getTasks(contactId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(taskResponse.get("tasks"));
  }

  @RequestMapping(value="contact/{contactId}/task", method=RequestMethod.POST, produces="application/json", consumes="application/json")
  @ResponseBody
  public ResponseEntity createTask(@PathVariable("contactId") String contactId, @RequestBody Map<String, Object> task) throws IOException {
    Map<String, Object> createdTask;
    try {
      createdTask = TaskService.createTask(contactId, task);
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
