package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.stream.events.XMLEvent;

class StaxStreamProcessorTest {

    @Test
    public void readCities() throws Exception {
        try (StaxStreamProcessor processor = new StaxStreamProcessor(
                Resources.getResource(StaxStreamProcessor.class, "/payload.xml").openStream())) {
            Assertions.assertTrue(processor.doUntil(XMLEvent.START_ELEMENT, "City"));
        }
    }

    @Test
    public void readCities2() throws Exception {
        try (StaxStreamProcessor processor = new StaxStreamProcessor(
                Resources.getResource(StaxStreamProcessor.class, "/payload.xml").openStream())) {
            String city;
            while ((city = processor.getElementValue("City")) != null) {
                System.out.println(city);
            }
        }
    }
}