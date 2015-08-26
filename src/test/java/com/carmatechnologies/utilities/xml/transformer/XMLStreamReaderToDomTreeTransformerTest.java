package com.carmatechnologies.utilities.xml.transformer;

import com.carmatechnologies.utilities.xml.common.AaltoXMLInputFactory;
import org.junit.Test;
import org.w3c.dom.Node;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerConfigurationException;

import static com.carmatechnologies.utilities.xml.TestingUtilities.moveToFirstElementNamed;
import static com.carmatechnologies.utilities.xml.TestingUtilities.removeWhitespaceNodes;
import static com.carmatechnologies.utilities.xml.TestingUtilities.streamFor;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class XMLStreamReaderToDomTreeTransformerTest {

    @Test
    public void xmlStreamReaderToDomTreeTransformerShouldOnlyTransformCurrentElementAsDomTree() throws XMLStreamException, TransformerConfigurationException {
        XMLStreamReader reader = AaltoXMLInputFactory.newInstance().createXMLStreamReader(streamFor("/books.xml"));
        moveToFirstElementNamed("book", reader);

        Node domTree = new XMLStreamReaderToDomTreeTransformer().apply(reader);

        removeWhitespaceNodes(domTree);
        assertThat(domTree, is(not(nullValue())));
        assertThat(domTree.getLocalName(), is(nullValue()));
        assertThat(domTree.getNodeValue(), is(nullValue()));
        assertThat(domTree.getChildNodes().getLength(), is(1));

        Node book = domTree.getFirstChild();
        assertThat(book, is(not(nullValue())));
        assertThat(book.getLocalName(), is("book"));
        assertThat(book.getNodeValue(), is(nullValue()));

        Node title = book.getFirstChild();
        assertThat(title, is(not(nullValue())));
        assertThat(title.getLocalName(), is("title"));
        assertThat(title.getNodeValue(), is(nullValue()));

        Node titleContent = title.getFirstChild();
        assertThat(titleContent, is(not(nullValue())));
        assertThat(titleContent.getLocalName(), is(nullValue()));
        assertThat(titleContent.getNodeValue(), is("Everyday Italian"));
    }

}
