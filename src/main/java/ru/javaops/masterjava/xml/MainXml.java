package ru.javaops.masterjava.xml;

import com.google.common.io.Resources;
import org.xml.sax.SAXException;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.Project;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;


import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainXml {
  public static void main(String[] args) throws JAXBException, IOException, SAXException {
    JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
    URL url = Resources.getResource("payload.xml");
    jaxbParser.setSchema(Schemas.ofClasspath("payload.xsd"));
    Payload payload = jaxbParser.unmarshal(url.openStream());
    List<Project> projects = payload.getProjects().getProject();
    projects.forEach(System.out::println);

  }
}
