package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import ru.javaops.masterjava.xml.schema.CityType;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

class JaxbParserTest {
    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);

    static {
        JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
    }

    @Test
    public void testPayload() throws Exception {
//        JaxbParserTest.class.getResourceAsStream("/city.xml")
//        Payload payload = JAXB_PARSER.unmarshal(
//                Resources.getResource("payload.xml").openStream());
        Payload payload = JAXB_PARSER.unmarshal(
                Resources.getResource(JaxbParserTest.class, "/payload.xml").openStream());
        String strPayload = JAXB_PARSER.marshal(payload);
        JAXB_PARSER.validate(strPayload);
        System.out.println(strPayload);
    }

    @Test
    public void testCity() throws Exception {
        JAXBElement<CityType> cityTypeElement = JAXB_PARSER.unmarshal(
                Resources.getResource(JaxbParserTest.class, "/city.xml").openStream());
        CityType cityType = cityTypeElement.getValue();
        JAXBElement<CityType> cityTypeElement2 = new JAXBElement<>(new QName("http://javaops.ru", "City"), CityType.class, cityType);
        String strCity = JAXB_PARSER.marshal(cityTypeElement2);
        JAXB_PARSER.validate(strCity);
        System.out.println(strCity);
    }
}