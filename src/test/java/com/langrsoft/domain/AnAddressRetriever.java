package com.langrsoft.domain;

import com.langrsoft.util.Http;
import com.langrsoft.util.HttpImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
// START:test
import static org.mockito.AdditionalMatchers.and;
// ...
// END:test
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnAddressRetriever {
   Http http = mock(Http.class);

   // START:test
   @Test
   void answersAppropriateAddressForValidCoordinates() {
      when(http.get(
         // START_HIGHLIGHT
         and(contains("lat=38.000000"), contains("lon=-104.000000"))))
         // END_HIGHLIGHT
         .thenReturn(
            // ...
            // END:test
            """
               {"address":{
                 "house_number":"324",
                 "road":"Main St",
                 "city":"Anywhere",
                 "state":"Colorado",
                 "postcode":"81234",
                 "country_code":"us"}}
               """);
      var retriever = new AddressRetriever(http);

      var address = retriever.retrieve(38, -104);

      assertEquals("324", address.house_number());
      assertEquals("Main St", address.road());
      assertEquals("Anywhere", address.city());
      assertEquals("Colorado", address.state());
      assertEquals("81234", address.postcode());
      // START:test
   }
   // END:test

   @Test
   void throwsWhenNotUSCountryCode() {
      when(http.get(anyString())).thenReturn("""
         {"address":{ "country_code":"not us"}}""");
      var retriever = new AddressRetriever(http);

      assertThrows(UnsupportedOperationException.class,
         () -> retriever.retrieve(1.0, -1.0));
   }

   @Disabled("retrieves as of 2024-Jun-11")
   @Tag("slow")
   @Test
   void liveIntegrationTest() {
      var retriever = new AddressRetriever(new HttpImpl());

      var address = retriever.retrieve(38.8372956, -104.8255679);

      assertEquals("North Cascade Avenue", address.road());
      assertEquals("Colorado Springs", address.city());
      assertEquals("Colorado", address.state());
      assertEquals("80903", address.postcode());
   }
}
