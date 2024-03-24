package com.langrsoft.util;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("slow")
class AnHttpImpl {
    static final String GET_ECHO =
        "http://httpbin.org/response-headers?Content-Type=text/plain&custom=%s";

    @Test
    void retrieves() {
        var result = new HttpImpl().get(format(GET_ECHO, "hello,%20Jeff!"));
        assertTrue(result.contains("hello, Jeff!"));
    }
}
