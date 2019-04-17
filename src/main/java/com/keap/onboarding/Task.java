package com.keap.onboarding;

public class Task {

  private final long id;
  private final String title;

  public Task(long id, String title) {
    this.id = id;
    this.title = title;
  }

  public long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }
}
