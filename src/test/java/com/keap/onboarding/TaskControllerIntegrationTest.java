package com.keap.onboarding;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(TaskController.class)
public class TaskControllerIntegrationTest {

  MockMvc mvc;

  @Test
  public void getTaskListGivenContactId() throws Exception{
    mvc.perform(get("/contact/4/task"))
        .andExpect(status().isOk());
  }

}
