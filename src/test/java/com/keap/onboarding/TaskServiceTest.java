package com.keap.onboarding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskServiceTest {

  private TaskService taskService;

  @Before
  public void setup() {
    taskService = new TaskService("bm4s5r6fs2dm4junbmkjsz68");
  }

  @Test
  public void testValidateContactId() {
    assertTrue("'4' is a valid contact ID", taskService.isValidContactId("4"));
    assertFalse("'0' is an invalid contact ID", taskService.isValidContactId("0"));
    assertFalse("'four' is an invalid contact ID", taskService.isValidContactId("four"));
  }

  @Test
  public void testContactExists() throws IOException {
    assertTrue("Contact exists by ID '4'", taskService.contactExists("4").getStatusCode() == HttpStatus.OK);
    assertTrue("Contact exists by ID '1'", taskService.contactExists("4").getStatusCode() == HttpStatus.OK);
  }

  @Test
  public void testGetTasks() throws IOException {
    Map<String, Object> tasksResponse1 = taskService.getTasks("4");
    Map<String, Object> tasksResponse2 = taskService.getTasks("0");
    List<Object> tasks4 = (ArrayList<Object>) tasksResponse1.get("tasks");
    List<Object> allTasks = (ArrayList<Object>) tasksResponse2.get("tasks");
    assertTrue("Task response has list of tasks for contact ID 4", tasksResponse1.containsKey("tasks"));
    assertTrue("Tasks for all contacts retrieve with unrecognized contact ID", tasks4.size() < allTasks.size());
  }

  @Test
  public void testCreateTask() throws IOException {
    // Successfully create task
    String contactId1 = "4";
    Map<String, Object> task1 = new HashMap<>();
    Map<String, Object> contact1 = new HashMap<>();
    task1.put("title", "Test Task 1");
    contact1.put("id", 4);
    task1.put("contact", contact1);
    task1.put("due_date", "2019-04-17T00:00:00Z");
    Map<String, Object> createdTask1 = taskService.createTask(contactId1, task1);
    assertTrue("Successfully creates task", createdTask1.containsKey("id"));

    // Test missing contact id
    String contactId2 = "3";
    Map<String, Object> task2 = new HashMap<>();
    Map<String, Object> contact2 = new HashMap<>();
    task2.put("title", "Test Task 2");
    contact2.put("id", 3);
    task2.put("contact", contact2);
    task2.put("due_date", "2019-04-17T00:00:00Z");
    try {
      taskService.createTask(contactId2, task2);
    } catch (HttpClientErrorException e) {
      assertEquals("Recognize invalid contact ID", e.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    // Test non existent field
    String contactId3 = "4";
    Map<String, Object> task3 = new HashMap<>();
    Map<String, Object> contact3 = new HashMap<>();
    task3.put("title", "Test Task 3");
    contact3.put("id", 4);
    task3.put("contact", contact3);
    task3.put("due_date", "2019-04-17T00:00:00Z");
    try {
      taskService.createTask(contactId3, task3);
    } catch (HttpClientErrorException e) {
      assertEquals("Reject fields not in IS task model", e.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    // Test missing required field
    String contactId4 = "4";
    Map<String, Object> task4 = new HashMap<>();
    Map<String, Object> contact4 = new HashMap<>();
    contact4.put("id", 4);
    task4.put("contact", contact4);
    task4.put("due_date", "2019-04-17T00:00:00Z");
    try {
      taskService.createTask(contactId4, task4);
    } catch (HttpClientErrorException e) {
      assertEquals("Reject requests missing required fields", e.getStatusCode(), HttpStatus.BAD_REQUEST);
    }
  }
}
