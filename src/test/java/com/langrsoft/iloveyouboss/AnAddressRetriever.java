package com.langrsoft.iloveyouboss;

import org.junit.jupiter.api.*;

import java.io.*;
import com.langrsoft.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;
// START:test
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
// ...
// END:test

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnAddressRetriever {
    @InjectMocks
    AddressRetriever retriever;
    @Mock
    Http http;
    @Mock
    Auditor auditor;

    @Test
    void answersAppropriateAddressForValidCoordinates()
        throws IOException {
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

    // START:test
    @Nested
    class Auditing {
        @Test
        void occursWhenNonUSAddressRetrieved() {
            when(http.get(anyString())).thenReturn("""
               {"address":{ "country_code":"not us"}}""");

            try { retriever.retrieve(1.0, -1.0); }
            catch (Exception expected) {}

            verify(auditor).audit("request for country code: not us");
        }

        @Test
        void doesNotOccurForUSCountryCode() throws IOException {
            when(http.get(anyString())).thenReturn("""
               {"address":{ "country_code":"us"}}""");

            retriever.retrieve(1.0, -1.0);

            verify(auditor, never()).audit(any());
        }
    }
    // ...
    // END:test

    @Test
    void returnsNullWhenHttpGetThrows() throws IOException {
        when(http.get(anyString())).thenThrow(RuntimeException.class);

        var address = retriever.retrieve(38, -104);

        assertNull(address);
    }

    @Disabled("works as of 2024-Mar-24")
    @Tag("slow")
    @Test
    void liveIntegrationTest() throws IOException {
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
