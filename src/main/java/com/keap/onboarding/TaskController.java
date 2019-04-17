package com.keap.onboarding;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Response;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
public class TaskController {

  private final String apiBaseURL = "https://api.infusionsoft.com/crm/rest/v1";
  private final String accessToken = "vrqckgfbdfpb38avc9vzetxf";

  @RequestMapping(value="contact/{contactId}/task", method=RequestMethod.GET, produces="application/json")
  public ResponseEntity getTasks(@PathVariable("contactId") String contactId) throws IOException {

    // Check if contactId is integer
    boolean isNumber = contactId.matches("^\\d+$");
    int contactIdInt = Integer.parseInt(contactId);
    if (!isNumber || contactIdInt <= 0) {
      Map<String, Object> response = new HashMap<>();
      response.put("message", "Contact Id must be a positive integer");
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(response);
    }

    // Check if contact exists by given id
    if (!contactExists(contactId)) {
      Map<String, Object> response = new HashMap<>();
      response.put("message", "Contact doesn't exist");
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(response);
    }

    // Get list of tasks
    RestTemplate restTemplate = new RestTemplate();
    String urlString2 = apiBaseURL + "/tasks?contact_id=" + contactId;
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.set("Host", "api.infusionsoft.com");
    headers.setBearerAuth(accessToken);
    HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

    ResponseEntity<String> response = restTemplate.exchange(urlString2, HttpMethod.GET, entity, String.class);
    String body = response.getBody();
    Map<String, Object> tasks;
    tasks = new ObjectMapper().readValue(body, Map.class);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(tasks.get("tasks"));
  }

  @RequestMapping(value="contact/{contactId}/task", method=RequestMethod.POST, produces="application/json", consumes="application/json")
  @ResponseBody
  public ResponseEntity createTask(@PathVariable("contactId") String contactId, @RequestBody Map<String, Object> task) throws IOException {
    RestTemplate restTemplate = new RestTemplate();
    String urlString = apiBaseURL + "/tasks";
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.set("Host", "api.infusionsoft.com");
    headers.setBearerAuth(accessToken);

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(task, headers);

    ResponseEntity<String> response;
    try {
      response = restTemplate.exchange(urlString, HttpMethod.POST, entity, String.class);
    } catch (HttpClientErrorException e) {
      return ResponseEntity
          .status(e.getStatusCode())
          .body(e.getResponseBodyAsString());
    }

    String responseBody = response.getBody();
    Map<String, Object> createdTask = new ObjectMapper().readValue(responseBody, HashMap.class);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdTask);
  }

  private boolean contactExists(String contactId) throws IOException {
    RestTemplate restTemplate = new RestTemplate();
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

}
