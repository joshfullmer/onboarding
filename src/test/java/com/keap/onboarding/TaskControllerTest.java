package com.keap.onboarding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.Silent.class)
@WebMvcTest(TaskController.class)
public class TaskControllerTest {

  private MockMvc mvc;

  @InjectMocks
  TaskController taskController;

  private String accessToken = "m3jecq5kdk9xnex85fe6x2yr";

  @Before
  public void setup() {
    mvc = MockMvcBuilders.standaloneSetup(taskController).build();
  }

  @Test
  public void testHasValidAccessTokenGetTask() throws Exception {
    MockHttpServletResponse response = mvc.perform(get("/contact/4/task"))
            .andReturn().getResponse();

    assertEquals("Response code is 401 UNAUTHORIZED", 401, response.getStatus());
  }

  @Test
  public void testHasOldInvalidAccessToken() throws Exception {
    MockHttpServletResponse response = mvc.perform(get("/contact/4/task?accessToken=bm4s5r6fs2dm4junbmkjsz68"))
            .andReturn().getResponse();

    assertEquals("Response code is 401 FORBIDDEN", 401, response.getStatus());
  }

  @Test
  public void getTaskListGivenContactId() throws Exception {
    MockHttpServletResponse response = mvc.perform(get("/contact/4/task?accessToken=" + accessToken))
            .andReturn().getResponse();

    assertEquals("Response code is 200 OK", 200, response.getStatus());
  }

  @Test
  public void testBadContactId() throws Exception {
    MockHttpServletResponse response = mvc.perform(get("/contact/four/task?accessToken=" + accessToken))
            .andReturn().getResponse();

    assertEquals("Response code is 400 BAD REQUEST", 400, response.getStatus());
  }

  @Test
  public void testNonExistentContactId() throws Exception {
    MockHttpServletResponse response = mvc.perform(get("/contact/3/task?accessToken=" + accessToken))
            .andReturn().getResponse();

    assertEquals("Response code is 404 NOT FOUND", 404, response.getStatus());
  }

  @Test
  public void testCreateTask() throws Exception {
    String task = "{\"title\":\"test\",\"contact\":{\"id\":4},\"due_date\":\"2019-04-18T00:00:00Z\"}";
    MockHttpServletResponse response = mvc.perform(post("/contact/4/task?accessToken=" + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(task))
            .andReturn().getResponse();

    assertEquals("Response code is 201 CREATED", 201, response.getStatus());
  }

  @Test
  public void testBadTaskContent() throws Exception {
    String task = "{\"contact\":{\"id\":4},\"due_date\":\"2019-04-18T00:00:00Z\"}";
    MockHttpServletResponse response = mvc.perform(post("/contact/4/task?accessToken=" + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(task))
            .andReturn().getResponse();

    assertEquals("Response code is 400 BAD REQUEST", 400, response.getStatus());
  }

  @Test
  public void testHasValidAccessTokenCreateTask() throws Exception {
    String task = "{\"title\":\"test\",\"contact\":{\"id\":4},\"due_date\":\"2019-04-18T00:00:00Z\"}";
    MockHttpServletResponse response = mvc.perform(post("/contact/4/task")
            .contentType(MediaType.APPLICATION_JSON)
            .content(task))
            .andReturn().getResponse();
  }

}
