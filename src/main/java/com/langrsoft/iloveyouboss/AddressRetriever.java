package com.langrsoft.iloveyouboss;

// START:AddressRetriever
import java.io.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langrsoft.util.Http;

import static java.lang.String.format;

public class AddressRetriever {
    private static final String SERVER = "http://nominatim.openstreetmap.org";
    // START_HIGHLIGHT
    private final Http http;
    // END_HIGHLIGHT

    // START_HIGHLIGHT
    public AddressRetriever(Http http) {
        this.http = http;
        // END_HIGHLIGHT
    }

    public Address retrieve(double latitude, double longitude)
        throws IOException {
        var locationParams = format("lat=%.6f&lon=%.6f", latitude, longitude);
        var url = format("%s/reverse?%s&format=json", SERVER, locationParams);

        // START_HIGHLIGHT
        var jsonResponse = http.get(url);
        // END_HIGHLIGHT

        var response = parseResponse(jsonResponse);

        var address = response.address();
        var country = address.country_code();
        if (!country.equals("us"))
            throw new UnsupportedOperationException("intl addresses unsupported");

        return address;
    }

    private Response parseResponse(String jsonResponse)
        throws JsonProcessingException {
        var mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonResponse, Response.class);
    }
}
// END:AddressRetriever
