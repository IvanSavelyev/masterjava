package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.stream.events.XMLEvent;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.io.InputStream;
import java.util.stream.IntStream;

class XPathProcessorTest {

    @Test
    public void readCities() throws Exception {
        InputStream inputStream = Resources.getResource(XPathProcessor.class, "/payload.xml").openStream();
        XPathProcessor xPathProcessor = new XPathProcessor(inputStream);
        XPathExpression expression = XPathProcessor.getExpression("/Payload/Cities/City/text()");
        NodeList nodeList = xPathProcessor.evaluate(expression, XPathConstants.NODESET);
        IntStream.range(0, nodeList.getLength()).forEach(i -> System.out.println(nodeList.item(i).getNodeValue()));
//        Assertions.assertEquals(3, nodeList.getLength());
    }

}