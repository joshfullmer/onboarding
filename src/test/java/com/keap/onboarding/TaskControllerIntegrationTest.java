package com.keap.onboarding;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(TaskController.class)
public class TaskControllerIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @Test
  public void getTaskListGivenContactId() {

  }

}
