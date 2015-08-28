package com.carmatechnologies.utilities.xml;

import com.carmatechnologies.utilities.xml.common.Pair;
import com.carmatechnologies.utilities.xml.predicate.XPathPredicate;
import com.carmatechnologies.utilities.xml.predicate.XPathSetPredicate;
import com.carmatechnologies.utilities.xml.transformer.DomTreeToOutputStreamTransformer;
import com.carmatechnologies.utilities.xml.transformer.XPathToOutputStreamTransformer;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Node;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.carmatechnologies.utilities.xml.TestingUtilities.streamFor;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class XmlStreamFilterTest {

    private static final String NEW_LINE = System.getProperty("line.separator");

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void filterShouldFilterBasedOnXPathQueryAndOutputFilteredElements() throws XPathExpressionException, TransformerConfigurationException, XMLStreamException, IOException {
        Predicate<Node> filter = new XPathPredicate("//book/tags/tag[text() = 'magician']");
        Function<Pair<Node, OutputStream>, Void> transformer = new DomTreeToOutputStreamTransformer();
        XmlStreamFilter streamFilter = new XmlStreamFilter("book", filter, transformer);

        InputStream in = streamFor("/books.xml");
        OutputStream out = new ByteArrayOutputStream();

        streamFilter.filter(in, out);

        assertThat(out.toString(), is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
                "<book category=\"CHILDREN\">" + NEW_LINE +
                "        <title lang=\"en\">Harry Potter</title>" + NEW_LINE +
                "        <author>J K. Rowling</author>" + NEW_LINE +
                "        <year>2005</year>" + NEW_LINE +
                "        <price>29.99</price>" + NEW_LINE +
                "        <tags>" + NEW_LINE +
                "            <tag>fantasy</tag>" + NEW_LINE +
                "            <tag>magician</tag>" + NEW_LINE +
                "        </tags>" + NEW_LINE +
                "    </book>"));
    }

    @Test
    public void filterShouldFilterBasedOnXPathQueryAndWhiteListSetAndOutputFilteredElementsAccordingToXPathTransformation() throws XPathExpressionException, TransformerConfigurationException, XMLStreamException, IOException {
        Predicate<Node> filter = new XPathSetPredicate("//book/tags/tag/text()", Sets.newHashSet("xml", "xquery"));
        Function<Pair<Node, OutputStream>, Void> transformer = new XPathToOutputStreamTransformer("//book/title/text()");
        XmlStreamFilter streamFilter = new XmlStreamFilter("book", filter, transformer);

        InputStream in = streamFor("/books.xml");
        OutputStream out = new ByteArrayOutputStream();

        streamFilter.filter(in, out);

        assertThat(out.toString(), is("XQuery Kick Start\nLearning XML\n"));
    }

    @Test
    public void nullElementLocalNameShouldThrowNullPointerException() throws XPathExpressionException, TransformerConfigurationException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(equalTo("XML element's local name must NOT be null."));

        new XmlStreamFilter(null, new XPathPredicate("//book/tags/tag[text() = 'magician']"), new DomTreeToOutputStreamTransformer());
    }

    @Test
    public void emptyElementLocalNameShouldThrowIllegalArgumentException() throws XPathExpressionException, TransformerConfigurationException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(equalTo("XML element's local name must NOT be empty."));

        new XmlStreamFilter("", new XPathPredicate("//book/tags/tag[text() = 'magician']"), new DomTreeToOutputStreamTransformer());
    }

}
