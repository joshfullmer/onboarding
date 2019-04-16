package com.keap.onboarding;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {
  @RequestMapping("/task/get")
  public String getTask(@RequestParam(value="id", defaultValue="0") String id) {
    if (id.equals("0")) {
      return "Hello";
    } else {
      return id;
    }
  }
}
