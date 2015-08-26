package com.carmatechnologies.utilities.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.google.common.base.Charsets.UTF_8;

public final class TestingUtilities {
    private TestingUtilities() {
        // Utility class, do NOT instantiate.
    }

    /**
     * Shorthand to get resource from classpath, by name, as stream.
     *
     * @param name Resource's name, e.g. "/books.xml"
     * @return instance of {@InputStream} corresponding to the specified resource.
     */
    public static InputStream streamFor(final String name) {
        return TestingUtilities.class.getResourceAsStream(name);
    }

    /**
     * Move to the first element which has the a local name equal to the provided one.
     *
     * @param localName localName of the element to reach.
     * @param reader    reader for the XML stream to process.
     * @throws XMLStreamException
     */
    public static void moveToFirstElementNamed(final String localName, final XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext())
            if ((reader.next() == XMLEvent.START_ELEMENT) && (localName.equals(reader.getLocalName())))
                break;
    }

    /**
     * Recursively remove empty/whitespace children Nodes from the provided DOM tree element.
     *
     * @param element DOM tree to prune.
     */
    public static void removeWhitespaceNodes(final Node element) {
        if (element == null)
            return;
        final NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node child = children.item(i);
            if ((child instanceof Text) && ((Text) child).getData().trim().length() == 0) {
                element.removeChild(child);
            } else if (child instanceof Element) {
                removeWhitespaceNodes(child);
            }
        }
    }

    /**
     * Helper method to parse the provided XML string as a DOM tree.
     *
     * @param xml XML string to parse.
     * @return DOM tree corresponding to the provided XML string.
     */
    public static Document parseDomTree(final String xml) {
        try {
            return newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }
}
