package com.langrsoft.iloveyouboss;

// START:Address
public record Address(
    String road, String city, String state,
    String country_code, String house_number, String postcode) {
}
// END:Address
