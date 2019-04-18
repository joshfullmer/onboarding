package com.keap.onboarding;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.Silent.class)
@WebMvcTest(TaskController.class)
public class TaskControllerIntegrationTest {

  private MockMvc mvc;

  @Mock
  TaskService taskService;

  @InjectMocks
  TaskController taskController;

  @Before
  public void setup() {
    mvc = MockMvcBuilders.standaloneSetup(taskController).build();
  }

  @Test
  public void getTaskListGivenContactId() throws Exception {
    MockHttpServletResponse response = mvc.perform(get("/contact/4/task"))
            .andReturn().getResponse();

    assertEquals("Response code is 200 OK", 200, response.getStatus());
  }

  @Test
  public void testBadContactId() throws Exception {
    MockHttpServletResponse response = mvc.perform(get("/contact/four/task"))
            .andReturn().getResponse();

    assertEquals("Response code is 400 BAD REQUEST", 400, response.getStatus());
  }

  @Test
  public void testNonExistentContactId() throws Exception {
    MockHttpServletResponse response = mvc.perform(get("/contact/0/task"))
            .andReturn().getResponse();

    assertEquals("Response code is 400 BAD REQUEST", 400, response.getStatus());
  }

  @Test
  public void testCreateTask() throws Exception {
    String task = "{\"title\":\"test\",\"contact\":{\"id\":4},\"due_date\":\"2019-04-18T00:00:00Z\"}";
    MockHttpServletResponse response = mvc.perform(post("/contact/4/task")
            .contentType(MediaType.APPLICATION_JSON)
            .content(task))
            .andReturn().getResponse();

    assertEquals("Response code is 201 CREATED", 201, response.getStatus());
  }

  @Test
  public void testBadTaskContent() throws Exception {
    String task = "{\"contact\":{\"id\":4},\"due_date\":\"2019-04-18T00:00:00Z\"}";
    MockHttpServletResponse response = mvc.perform(post("/contact/4/task")
            .contentType(MediaType.APPLICATION_JSON)
            .content(task))
            .andReturn().getResponse();

    assertEquals("Response code is 400 BAD REQUEST", 400, response.getStatus());
  }

}
