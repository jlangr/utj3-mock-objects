package com.langrsoft.iloveyouboss;

// START:AddressRetriever
import java.io.*;

import com.langrsoft.util.HttpImpl;
import org.json.simple.*;
import org.json.simple.parser.*;

public class AddressRetriever {
   public Address retrieve(double latitude, double longitude)
         throws IOException, ParseException {
      var parms = String.format("lat=%.6flon=%.6f", latitude, longitude);
      // START_HIGHLIGHT
      var response = new HttpImpl().get(
        "http://open.mapquestapi.com/nominatim/v1/reverse?format=json&"
        + parms);
      // END_HIGHLIGHT

      var obj = (JSONObject)new JSONParser().parse(response);

      var address = (JSONObject)obj.get("address");
      var country = (String)address.get("country_code");
      if (!country.equals("us"))
         throw new UnsupportedOperationException(
            "cannot support non-US addresses at this time");

      var houseNumber = (String)address.get("house_number");
      var road = (String)address.get("road");
      var city = (String)address.get("city");
      var state = (String)address.get("state");
      var zip = (String)address.get("postcode");
      return new Address(houseNumber, road, city, state, zip);
   }
}
// END:AddressRetriever
