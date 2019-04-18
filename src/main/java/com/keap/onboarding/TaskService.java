package com.keap.onboarding;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class TaskService {

  private final String apiBaseURL = "https://api.infusionsoft.com/crm/rest/v1";
  private final String accessToken = "bm4s5r6fs2dm4junbmkjsz68";

  @Autowired
  RestTemplate restTemplate = new RestTemplate();

  public boolean isValidContactId(String contactId) {
    boolean isNumber = contactId.matches("^\\d+$");
    int contactIdInt;
    try {
      contactIdInt = Integer.parseInt(contactId);
    } catch (NumberFormatException e) {
      return false;
    }
    return isNumber && contactIdInt > 0;
  }

  public boolean contactExists(String contactId) throws IOException {
    String urlString = apiBaseURL + "/contacts/" + contactId;
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.set("Host", "api.infusionsoft.com");
    headers.setBearerAuth(accessToken);

    HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

    ResponseEntity<String> response;
    try {
      response = restTemplate.exchange(urlString, HttpMethod.GET, entity, String.class);
    } catch (HttpClientErrorException e) {
      return false;
    }

    return response.getStatusCode() == HttpStatus.OK;
  }

  public Map<String, Object> getTasks(String contactId) throws IOException {
    String urlString = apiBaseURL + "/tasks?contact_id=" + contactId;
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.set("Host", "api.infusionsoft.com");
    headers.setBearerAuth(accessToken);
    HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

    ResponseEntity<String> response = restTemplate.exchange(urlString, HttpMethod.GET, entity, String.class);
    String body = response.getBody();
    return new ObjectMapper().readValue(body, Map.class);
  }

  public Map<String, Object> createTask(String contactId, Map<String, Object> task) throws IOException, HttpClientErrorException {
    RestTemplate restTemplate = new RestTemplate();
    String urlString = apiBaseURL + "/tasks";
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.set("Host", "api.infusionsoft.com");
    headers.setBearerAuth(accessToken);

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(task, headers);

    ResponseEntity<String> response;
    response = restTemplate.exchange(urlString, HttpMethod.POST, entity, String.class);

    String responseBody = response.getBody();
    return new ObjectMapper().readValue(responseBody, HashMap.class);
  }

}
