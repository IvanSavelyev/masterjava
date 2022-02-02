package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import ru.javaops.masterjava.xml.schema.CityType;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

class StaxStreamProcessorTest {

    @Test
    public void readCities() throws Exception {
        try (StaxStreamProcessor processor = new StaxStreamProcessor(
                Resources.getResource(StaxStreamProcessor.class, "/payload.xml").openStream())) {
            XMLStreamReader reader = processor.getReader();

            while (reader.hasNext()) {
                int event = reader.next();
                if(event == XMLEvent.START_ELEMENT){
                    if("City".equals(reader.getLocalName())){
                        System.out.println(reader.getElementText());
                    }
                }
            }


        }
//        StaxStreamProcessor processor = new StaxStreamProcessor(
//                Resources.getResource(StaxStreamProcessor.class, "/city.xml").openStream());
//        Resources.getResource(StaxStreamProcessor.class, "/city.xml").openStream();
//        try (StaxStreamProcessor processor = new StaxStreamProcessor(
//                Resources.getResource(StaxStreamProcessorTest.class, "/city.xml")).getReader()) {
//
//        }
    }

}