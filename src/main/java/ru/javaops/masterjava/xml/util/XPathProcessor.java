package ru.javaops.masterjava.xml.util;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;

public class XPathProcessor {

    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    private static final DocumentBuilder DOCUMENT_BUILDER;

    private static final XPathFactory X_PATH_FACTORY = XPathFactory.newInstance();

    private static final XPath X_PATH = X_PATH_FACTORY.newXPath();

    static {
        DOCUMENT_BUILDER_FACTORY.setNamespaceAware(false);
        try {
            DOCUMENT_BUILDER = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private final Document document;

    public XPathProcessor(InputStream inputStream) {
        try{
            document = DOCUMENT_BUILDER.parse(inputStream);
        } catch (SAXException | IOException e ) {
            throw new IllegalArgumentException(e);
        }
    }

    public static synchronized XPathExpression getExpression(String exp) {
        try{
            return X_PATH.compile(exp);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public <T> T evaluate(XPathExpression expression, QName type) {
        try{
            return (T) expression.evaluate(document, type);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
