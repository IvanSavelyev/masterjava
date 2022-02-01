package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;

class JaxbParserTest {

    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);

    static {
        JAXB_PARSER.setSchema(Schemas.ofClasspath("/payload.xsd"));
    }

    @Test
    public void testPayload() throws Exception {
        Payload payload = JAXB_PARSER.unmarshal(
                Resources.getResource(JaxbParserTest.class, "/payload.xml").openStream()
        );
        String srtPayload = JAXB_PARSER.marshal(payload);
        JAXB_PARSER.validate(srtPayload);
        System.out.println(srtPayload);
    }

}