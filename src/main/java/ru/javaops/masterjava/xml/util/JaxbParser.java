package ru.javaops.masterjava.xml.util;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import java.io.*;

public class JaxbParser {

    protected JaxbMarshaller jaxbMarshaller;
    protected JaxbUnmarshaller jaxbUnmarshaller;
    protected Schema schema;

    public JaxbParser(Class... classesToBeBound) {
        try {
            init(JAXBContext.newInstance(classesToBeBound));
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public JaxbParser(String context) {
        try {
            init(JAXBContext.newInstance(context));
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void init(JAXBContext context) throws JAXBException {
        jaxbMarshaller = new JaxbMarshaller(context);
        jaxbUnmarshaller = new JaxbUnmarshaller(context);
    }

    public <T> T unmarshal(InputStream inputStream) throws JAXBException {
        return (T) jaxbUnmarshaller.unmarshal(inputStream);
    }

    public <T> T unmarshal(Reader reader) throws JAXBException {
        return (T) jaxbUnmarshaller.unmarshal(reader);
    }

    public <T> T unmarshal(String string) throws JAXBException {
        return (T) jaxbUnmarshaller.unmarshal(string);
    }

    public void setMarshallerProperty(String property, Object value) {
        try {
            jaxbMarshaller.setProperty(property, value);
        } catch (PropertyException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public synchronized String marshal(Object instance) throws JAXBException {
        return jaxbMarshaller.marshal(instance);
    }

    public synchronized void marshal(Object instance, Writer writer) throws JAXBException {
        jaxbMarshaller.marshal(instance, writer);
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
        jaxbMarshaller.setSchema(schema);
        jaxbUnmarshaller.setSchema(schema);
    }

    public void validate(String string) throws IOException, SAXException {
        validate(new StringReader(string));
    }

    public void validate(Reader reader) throws IOException, SAXException {
        schema.newValidator().validate(new StreamSource(reader));
    }
}
