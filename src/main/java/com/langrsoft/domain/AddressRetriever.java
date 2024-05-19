package com.langrsoft.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langrsoft.util.Http;
import com.langrsoft.util.HttpImpl;

// START:class
public class AddressRetriever {
    private static final String SERVER = "http://nominatim.openstreetmap.org";
    // START_HIGHLIGHT
    private Http http = new HttpImpl(); // this cannot be final
    // END_HIGHLIGHT

    // START_HIGHLIGHT
    // look ma, no constructor!
    // END_HIGHLIGHT

    public Address retrieve(double latitude, double longitude) {
        // ...
// END:class
        var locationParams = "lat=%.6f&lon=%.6f".formatted(latitude, longitude);
        var url = "%s/reverse?%s&format=json".formatted(SERVER, locationParams);

        var jsonResponse = http.get(url);

        var response = parseResponse(jsonResponse);

        var address = response.address();
        var country = address.country_code();
        if (!country.equals("us"))
            throw new UnsupportedOperationException("intl addresses unsupported");

        return address;
        // START:class
    }
    // ...
    // END:class

    private Response parseResponse(String jsonResponse) {
        var mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
       try {
          return mapper.readValue(jsonResponse, Response.class);
       } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
       }
    }
    // START:class
}
// END:class
