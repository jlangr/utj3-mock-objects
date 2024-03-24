package com.langrsoft.util;

// START:HttpImpl
import java.io.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.*;

public class HttpImpl implements Http {
   public String get(String url) {
      var client = HttpClients.createDefault();
      var request = new HttpGet(url);
      try {
         try (var response = client.execute(request)) {
             return EntityUtils.toString(response.getEntity());
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}
// END:HttpImpl
