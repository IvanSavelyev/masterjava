package ru.javaops.masterjava.xml.util;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;


public class StaxStreamProcessor implements AutoCloseable {
    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();

    private final XMLStreamReader reader;

    public StaxStreamProcessor(InputStream inputStream) throws XMLStreamException {
        reader = FACTORY.createXMLStreamReader(inputStream);
    }

    public XMLStreamReader getReader() {
        return reader;
    }

    public boolean doUntil(int stopEvent, String value) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if(event == stopEvent){
                if(value.equals(getValue(event))){
                    return true;
                }
            }
        }
        return false;
    }

    public String getElementValue(String element) throws XMLStreamException {
        return doUntil(XMLStreamReader.START_ELEMENT, element) ? reader.getElementText() : null;
    }

    private String getValue(int event) {
        return event == XMLEvent.CHARACTERS ? reader.getText() : reader.getLocalName();
    }

    @Override
    public void close() throws Exception {
        if(reader != null) {
            try {
                reader.close();
            } catch (XMLStreamException e) {

            }
        }
    }
}
