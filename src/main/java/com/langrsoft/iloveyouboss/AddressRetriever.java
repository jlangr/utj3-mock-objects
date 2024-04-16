package com.langrsoft.iloveyouboss;

// START:AddressRetriever
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langrsoft.util.HttpImpl;
import java.io.IOException;
import static java.lang.String.format;

public class AddressRetriever {
    private static final String SERVER = "http://nominatim.openstreetmap.org";

    public Address retrieve(double latitude, double longitude) {
        var locationParams = format("lon=%.6f&lat=%.6f", latitude, longitude);
        var url = format("%s/reverse?%s&format=json", SERVER, locationParams);

        // START_HIGHLIGHT
        var jsonResponse = new HttpImpl().get(url);
        // END_HIGHLIGHT

        var response = parseResponse(jsonResponse);

        var address = response.address();
        var country = address.country_code();
        if (!country.equals("us"))
            throw new UnsupportedOperationException("intl addresses unsupported");

        return address;
    }

    private Response parseResponse(String jsonResponse) {
        var mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
       try {
          return mapper.readValue(jsonResponse, Response.class);
       } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
       }
    }
}
// END:AddressRetriever
