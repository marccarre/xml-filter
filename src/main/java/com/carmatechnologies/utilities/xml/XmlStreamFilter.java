package com.carmatechnologies.utilities.xml;

import com.carmatechnologies.utilities.xml.common.MutablePair;
import com.carmatechnologies.utilities.xml.common.Pair;
import com.carmatechnologies.utilities.xml.common.XMLInputFactoryImpl;
import com.carmatechnologies.utilities.xml.transformer.XMLStreamReaderToDomTreeTransformer;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.w3c.dom.Node;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerConfigurationException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.carmatechnologies.utilities.xml.common.InputStreams.autoGUnzip;
import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@code XmlStreamFilter} allows you to:
 * - stream-process large files, but  nevertheless
 * - filter elements with a specific name, or validating the condition specified by the provided predicate, and
 * - transform filtered elements according to the provided transformer.
 */
public final class XmlStreamFilter implements StreamFilter {
    public static final String VERSION = "1.0";

    private final String elementLocalName;
    private final Predicate<Node> filter;
    private final Function<Pair<Node, OutputStream>, Void> transformer;
    private final XMLInputFactory xmlInputFactory;
    private final Function<XMLStreamReader, Node> domTreeTransformer;

    public XmlStreamFilter(final String elementLocalName, final Predicate<Node> filter, final Function<Pair<Node, OutputStream>, Void> transformer) throws TransformerConfigurationException {
        this(elementLocalName, filter, transformer, XMLInputFactoryImpl.newInstance(), new XMLStreamReaderToDomTreeTransformer());
    }

    public XmlStreamFilter(final String elementLocalName, final Predicate<Node> filter, final Function<Pair<Node, OutputStream>, Void> transformer, final XMLInputFactory xmlInputFactory, final Function<XMLStreamReader, Node> domTreeTransformer) throws TransformerConfigurationException {
        checkNotNull(elementLocalName, "XML element's local name must NOT be null.");
        checkArgument(!elementLocalName.isEmpty(), "XML element's local name must NOT be empty.");
        this.elementLocalName = elementLocalName;
        this.filter = checkNotNull(filter, "Filter must NOT be null.");
        this.transformer = checkNotNull(transformer, "Transformer must NOT be null.");
        this.xmlInputFactory = checkNotNull(xmlInputFactory, "XMLInputFactory must NOT be null.");
        this.domTreeTransformer = checkNotNull(domTreeTransformer, "XMLStreamReader-to-DOM tree transformer must NOT be null.");
    }

    @Override
    public void filter(final InputStream in, final OutputStream out) {
        checkNotNull(in, "InputStream must NOT be null.");
        checkNotNull(out, "OutputStream must NOT be null.");
        try {
            doFilter(in, out);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Failed to filter the provided stream.", e);
        }
    }

    private void doFilter(final InputStream in, final OutputStream out) throws XMLStreamException {
        final XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(in, UTF_8.name());
        final MutablePair<Node, OutputStream> outputHolder = MutablePair.withSecond(out);

        while (reader.hasNext()) {
            if (isTargetElement(reader)) {
                final Node domTree = domTreeTransformer.apply(reader);
                if (filter.apply(domTree)) {
                    transformer.apply(outputHolder.first(domTree));
                }
            }
        }
    }

    private boolean isTargetElement(final XMLStreamReader reader) throws XMLStreamException {
        return (reader.next() == XMLEvent.START_ELEMENT) && elementLocalName.equals(reader.getLocalName());
    }

    public static void main(final String[] args) throws IOException {
        final StreamFilter streamFilter = new XmlStreamFilterCliFactory().newStreamFilter(args);
        streamFilter.filter(new BufferedInputStream(autoGUnzip(System.in)), new BufferedOutputStream(System.out));
    }
}
