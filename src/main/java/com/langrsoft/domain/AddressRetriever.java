package com.langrsoft.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langrsoft.util.Http;
import com.langrsoft.util.HttpImpl;

// START:impl
public class AddressRetriever {
    private Auditor auditor = new ApplicationAuditor();
    private static final String SERVER = "http://nominatim.openstreetmap.org";
    private Http http = new HttpImpl(); // this cannot be final

    // START:impl
    public Address retrieve(double latitude, double longitude) {
    // ...
    // END:impl
        var locationParams = "lat=%.6f&lon=%.6f".formatted(latitude, longitude);
        var url = "%s/reverse?%s&format=json".formatted(SERVER, locationParams);

        // START:impl
        var jsonResponse = get(url);
        if (jsonResponse == null) return null;
        // ...
        // END:impl

        var response = parseResponse(jsonResponse);

        var address = response.address();
        var country = address.country_code();
        if (!country.equals("us")) {
            auditor.audit("request for country code: %s".formatted(country));
            throw new UnsupportedOperationException("intl addresses unsupported");
        }

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


    private Response parseResponse(String jsonResponse) {
        var mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(jsonResponse, Response.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    // START:impl
    // ...
}
// END:impl
