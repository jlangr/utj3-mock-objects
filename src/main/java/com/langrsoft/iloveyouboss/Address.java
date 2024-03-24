package com.langrsoft.iloveyouboss;

// START:Address
public record Address(
    String road, String city, String state, String zip, String houseNumber) {

    @Override
    public String toString() {
        return houseNumber() + " " +
            road() + ", " +
            city() + " " +
            state() + " " +
            zip();
    }
}
// END:Address
