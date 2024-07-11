package com.langrsoft.domain;

import com.langrsoft.util.Http;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnAddressRetriever {
   @InjectMocks
   AddressRetriever retriever;
   @Mock
   Http http;
   @Mock
   Auditor auditor;

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

   // START:test
   @Test
   void returnsNullWhenHttpGetThrows() {
      // START_HIGHLIGHT
      when(http.get(anyString())).thenThrow(RuntimeException.class);
      // END_HIGHLIGHT

      var address = retriever.retrieve(38, -104);

      assertNull(address);
   }
   // END:test

   @Nested
   class Auditing {
      @Test
      void occursWhenNonUSAddressRetrieved() {
         when(http.get(anyString())).thenReturn("""
           {"address":{ "country_code":"not us"}}""");

         assertThrows(UnsupportedOperationException.class,
            () -> retriever.retrieve(1.0, -1.0));

         verify(auditor).audit("request for country code: not us");
      }

      @Test
      void doesNotOccurWhenUSAddressRetrieved() {
         when(http.get(anyString())).thenReturn("""
            {"address":{ "country_code":"us"}}""");

         retriever.retrieve(1.0, -1.0);

         verify(auditor, never()).audit(any());
      }

      private void swallowExpectedException(Runnable r) {
         try {
            r.run();
         } catch (Exception expected) {
         }
      }
   }

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
}
