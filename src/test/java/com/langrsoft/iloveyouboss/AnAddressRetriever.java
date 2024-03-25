package com.langrsoft.iloveyouboss;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import java.io.*;
import com.langrsoft.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AnAddressRetriever {
    // START:test
    @Test
    void answersAppropriateAddressForValidCoordinates()
        throws IOException {
        Http http = (String url) -> {
            // START_HIGHLIGHT
            if (!url.contains("lat=38") ||
                !url.contains("lon=-104"))
                fail("url " + url + " does not contain correct params");
            // END_HIGHLIGHT
            return """
                {"address":{
                  "house_number":"324",
                  "road":"Main St",
                  "city":"Anywhere",
                  "state":"Colorado",
                  "postcode":"81234",
                  "country_code":"us"}}
                """;
        };
        var retriever = new AddressRetriever(http);

        var address = retriever.retrieve(38, -104);
        // ...
        // END:test

        assertEquals("324", address.house_number());
        assertEquals("Main St", address.road());
        assertEquals("Anywhere", address.city());
        assertEquals("Colorado", address.state());
        assertEquals("81234", address.postcode());
        // START:test
    }
// END:test

    // START:throws
    @Test
    void throwsWhenNotUSCountryCode() {
        Http http = (String url) -> """
            {"address":{ "country_code":"not us"}}""";
        var retriever = new AddressRetriever(http);

        assertThrows(UnsupportedOperationException.class,
            () -> retriever.retrieve(1.0, -1.0));
    }
    // END:throws

    @Disabled("works as of 2024-Mar-24")
    @Tag("slow")
    @Test
    void liveIntegrationTest() throws IOException {
        var retriever = new AddressRetriever(new HttpImpl());

        var address = retriever.retrieve(38.8372956, -104.8255679);

        assertEquals("North Cascade Avenue", address.road());
        assertEquals("Colorado Springs", address.city());
        assertEquals("Colorado", address.state());
        assertEquals("80903", address.postcode());
    }
    // START:test
}
// END:test
