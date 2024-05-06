package com.langrsoft.domain;

import org.junit.jupiter.api.*;

import com.langrsoft.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;
// START:test
// START_HIGHLIGHT
import static org.mockito.Mockito.verify;
// END_HIGHLIGHT
// ...
// END:test
// START:test2
// START_HIGHLIGHT
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
// END_HIGHLIGHT
// ...
// END:test2
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

// START:test
@ExtendWith(MockitoExtension.class)
class AnAddressRetriever {
    @InjectMocks
    AddressRetriever retriever;

    // START_HIGHLIGHT
    @Mock
    Auditor auditor;
    // END_HIGHLIGHT
    // ...
    // END:test
    @Mock
    Http http;

    @Test
    void answersAppropriateAddressForValidCoordinates() {
        when(http.get(contains("lat=38.000000&lon=-104.000000"))).thenReturn(
            """
                {"address":{
                  "house_number":"324",
                  "road":"Main St",
                  "city":"Anywhere",
                  "state":"Colorado",
                  "postcode":"81234",
                  "country_code":"us"}}
                """);

        var address = retriever.retrieve(38, -104);

        assertEquals("324", address.house_number());
        assertEquals("Main St", address.road());
        assertEquals("Anywhere", address.city());
        assertEquals("Colorado", address.state());
        assertEquals("81234", address.postcode());
    }

    @Test
    void throwsWhenNotUSCountryCode() {
        when(http.get(anyString())).thenReturn("""
            {"address":{ "country_code":"not us"}}""");

        assertThrows(UnsupportedOperationException.class,
            () -> retriever.retrieve(1.0, -1.0));
    }

    @Nested
    class Auditing {
        // START:test
    @Test
    void occursWhenNonUSAddressRetrieved() {
        when(http.get(anyString())).thenReturn("""
           {"address":{ "country_code":"not us"}}""");

        swallowExpectedException(() -> retriever.retrieve(1.0, -1.0));

        // START_HIGHLIGHT
        verify(auditor).audit("request for country code: not us");
        // END_HIGHLIGHT
    }

    private void swallowExpectedException(Runnable r) {
        try { r.run(); } catch (Exception expected) {}
    }
        // END:test

    // START:test2
    @Test
    void doesNotOccurWhenUSAddressRetrieved() {
        when(http.get(anyString())).thenReturn("""
           {"address":{ "country_code":"us"}}""");

        retriever.retrieve(1.0, -1.0);

        // START_HIGHLIGHT
        verify(auditor, never()).audit(any());
        // END_HIGHLIGHT
    }
    // END:test2
    }
    // START:test
    // ...
    // END:test

    @Disabled("works as of 2024-Mar-24")
    @Tag("slow")
    @Test
    void liveIntegrationTest() {
        var retriever = new AddressRetriever();

        var address = retriever.retrieve(38.8372956, -104.8255679);

        assertEquals("North Cascade Avenue", address.road());
        assertEquals("Colorado Springs", address.city());
        assertEquals("Colorado", address.state());
        assertEquals("80903", address.postcode());
    }
    // START:test
}
// END:test
