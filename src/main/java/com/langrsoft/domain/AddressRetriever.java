package com.langrsoft.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langrsoft.util.Http;
import com.langrsoft.util.HttpImpl;

// START:impl
public class AddressRetriever {
    // START_HIGHLIGHT
    private Auditor auditor = new ApplicationAuditor();
    // END_HIGHLIGHT
    // ...
    // END:impl
    private static final String SERVER = "http://nominatim.openstreetmap.org";
    private Http http = new HttpImpl(); // this cannot be final

    // START:impl
    public Address retrieve(double latitude, double longitude) {
    // ...
    // END:impl
        var locationParams = "lat=%.6f&lon=%.6f".formatted(latitude, longitude);
        var url = "%s/reverse?%s&format=json".formatted(SERVER, locationParams);

        var jsonResponse = http.get(url);
        if (jsonResponse == null) return null;

        var response = parseResponse(jsonResponse);

        var address = response.address();
        // START:impl
        var country = address.country_code();
        if (!country.equals("us")) {
            // START_HIGHLIGHT
            auditor.audit("request for country code: %s".formatted(country));
            // END_HIGHLIGHT
            throw new UnsupportedOperationException(
               "intl addresses unsupported");
        }

        return address;
    }
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
