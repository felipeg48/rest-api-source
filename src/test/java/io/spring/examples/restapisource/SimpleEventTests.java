package io.spring.examples.restapisource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class SimpleEventTests {

    @Autowired
    private JacksonTester<SimpleEvent> jacksonTester;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void jsonTest() throws IOException, ParseException {
        String jsonEvent = "{\"apiVersion\":\"v1.0\",\"actionRequested\":\"createEvent\",\"data\":\"Some data\",\"created\":\"2022-05-26 13:31:40\"}";
        SimpleEvent expectedEvent = new SimpleEvent("v1.0","createEvent","Some data",formatter.parse("2022-05-26 13:31:40") );

        SimpleEvent eventRequest = jacksonTester.parseObject(jsonEvent);

        assertThat(eventRequest.getApiVersion()).isEqualTo(expectedEvent.getApiVersion());
    }
}
