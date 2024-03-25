package com.langrsoft.iloveyouboss;

import java.io.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langrsoft.util.Http;

import static java.lang.String.format;

public class AddressRetriever {
    private static final String SERVER = "http://nominatim.openstreetmap.org";
    private final Http http;

    public AddressRetriever(Http http) {
        this.http = http;
    }

    // START:test
    public Address retrieve(double latitude, double longitude)
        throws IOException {
        // START_HIGHLIGHT
        var locationParams = format("lat=%.6f&lon=%.6f", latitude, longitude);
        // END_HIGHLIGHT
        var url = format("%s/reverse?%s&format=json", SERVER, locationParams);

        var jsonResponse = http.get(url);
        // ...
        // END:test

        var response = parseResponse(jsonResponse);

        var address = response.address();
        var country = address.country_code();
        if (!country.equals("us"))
            throw new UnsupportedOperationException("intl addresses unsupported");

        return address;
        // START:test
    }
    // END:test

    private Response parseResponse(String jsonResponse)
        throws JsonProcessingException {
        var mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonResponse, Response.class);
    }
}
