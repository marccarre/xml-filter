package com.carmatechnologies.utilities.xml.transformer;

import org.junit.Test;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static com.carmatechnologies.utilities.xml.TestingUtilities.parseDomTree;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DomTreeToOutputStreamTransformerTest {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private static final String XML = "<book category=\"COOKING\">" + NEW_LINE +
            "  <title lang=\"en\">Everyday Italian</title>" + NEW_LINE +
            "  <author>Giada De Laurentiis</author>" + NEW_LINE +
            "  <year>2005</year>" + NEW_LINE +
            "  <price>30.00</price>" + NEW_LINE +
            "  <tags>" + NEW_LINE +
            "    <tag>italian</tag>" + NEW_LINE +
            "    <tag>food</tag>" + NEW_LINE +
            "    <tag>pasta</tag>" + NEW_LINE +
            "  </tags>" + NEW_LINE +
            "</book>";

    @Test
    public void domTreeToOutputStreamShouldWriteValidAndExpectedXML() throws TransformerConfigurationException {
        Node domTree = parseDomTree(XML);
        OutputStream out = new ByteArrayOutputStream();

        new DomTreeToOutputStreamTransformer(out).apply(domTree);

        assertThat(out.toString(), is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + XML));
    }

}
