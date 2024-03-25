package com.langrsoft.iloveyouboss;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
// START:test
import java.io.*;
import com.langrsoft.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AnAddressRetriever {
    @Test
    void answersAppropriateAddressForValidCoordinates()
        throws IOException {
        // START:httpStub
        Http http = (String url) ->
            """
                {"address":{
                  "house_number":"324",
                  "road":"Main St",
                  "city":"Anywhere",
                  "state":"Colorado",
                  "postcode":"81234",
                  "country_code":"us"}}
                """;
        // END:httpStub
        var retriever = new AddressRetriever(http);

        var address = retriever.retrieve(38, -104);

        assertEquals("324", address.house_number());
        assertEquals("Main St", address.road());
        assertEquals("Anywhere", address.city());
        assertEquals("Colorado", address.state());
        assertEquals("81234", address.postcode());
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

    @Disabled("surprise!")
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
