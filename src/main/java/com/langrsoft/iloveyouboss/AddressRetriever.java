package com.langrsoft.iloveyouboss;

import java.io.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langrsoft.util.Http;
import com.langrsoft.util.HttpImpl;

import static java.lang.String.format;

public class AddressRetriever {
    private static final String SERVER = "http://nominatim.openstreetmap.org";
    private Http http = new HttpImpl(); // this cannot be final

    // START:impl
    public Address retrieve(double latitude, double longitude)
        throws IOException {
        var locationParams = format("lat=%.6f&lon=%.6f", latitude, longitude);
        var url = format("%s/reverse?%s&format=json", SERVER, locationParams);

        // START_HIGHLIGHT
        var jsonResponse = get(url);
        if (jsonResponse == null) return null;
        // END_HIGHLIGHT
        // ...
        // END:impl

        var response = parseResponse(jsonResponse);

        var address = response.address();
        var country = address.country_code();
        if (!country.equals("us"))
            throw new UnsupportedOperationException("intl addresses unsupported");

        return address;
        // START:impl
    }

    // START_HIGHLIGHT
    private String get(String url) {
        try {
            return http.get(url);
        }
        catch (Exception e) {
            return null;
        }
    }
    // END_HIGHLIGHT
    // END:impl

    private Response parseResponse(String jsonResponse)
        throws JsonProcessingException {
        var mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonResponse, Response.class);
    }
}
