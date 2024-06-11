package com.langrsoft.domain;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import com.langrsoft.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.and;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;
// START:test
// ...
// START_HIGHLIGHT
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
// END_HIGHLIGHT

// START_HIGHLIGHT
@ExtendWith(MockitoExtension.class)
   // END_HIGHLIGHT
class AnAddressRetriever {
    // START_HIGHLIGHT
    @InjectMocks
    AddressRetriever retriever;
    // END_HIGHLIGHT

    // START_HIGHLIGHT
    @Mock
    Http http;
    // END_HIGHLIGHT

    @Test
    void answersAppropriateAddressForValidCoordinates() {
        when(http.get(and(contains("lat=38.000000"),
                          contains("lon=-104.000000"))))
           // ...
           // END:test
           .thenReturn("""
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
        // START:test
    }
    // ...
// END:test

    // START:throws
    @Test
    void throwsWhenNotUSCountryCode() {
        when(http.get(anyString())).thenReturn("""
            {"address":{ "country_code":"not us"}}""");

        assertThrows(UnsupportedOperationException.class,
            () -> retriever.retrieve(1.0, -1.0));
    }
    // END:throws

   @Disabled("retrieves as of 2024-Jun-11")
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
