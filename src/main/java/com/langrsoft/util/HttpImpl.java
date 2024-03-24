package com.langrsoft.util;

// START:HttpImpl
import java.io.*;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.*;

public class HttpImpl implements Http {

   // TODO: replace JSON Simple!

   public String get(String url) throws IOException {
      var client = HttpClients.createDefault();
      var request = new HttpGet(url);
      var response = client.execute(request);
      try {
         var entity = response.getEntity();
         return EntityUtils.toString(entity);
      } finally {
         response.close();
      }
   }
}
// END:HttpImpl
