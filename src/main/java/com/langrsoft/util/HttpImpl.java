package com.langrsoft.util;

// START:HttpImpl
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

class HttpImpl implements Http {

  public String get(String url) {
    try (var client = HttpClient.newHttpClient()) {
      var request = HttpRequest.newBuilder().uri(URI.create(url)).build();
      try {
        var httpResponse = client.send(request, BodyHandlers.ofString());
        return httpResponse.body();
      } catch (IOException | InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
// END:HttpImpl
