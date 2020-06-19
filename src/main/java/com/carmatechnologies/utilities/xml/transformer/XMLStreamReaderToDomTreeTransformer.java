package com.carmatechnologies.utilities.xml.transformer;

import com.carmatechnologies.utilities.xml.common.TransformerFactoryImpl;
import org.w3c.dom.Node;

import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stax.StAXSource;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

public final class XMLStreamReaderToDomTreeTransformer implements Function<XMLStreamReader, Node> {
    private final Transformer transformer;

    public XMLStreamReaderToDomTreeTransformer(final Transformer transformer) {
        this.transformer = checkNotNull(transformer, "Transformer must NOT be null.");
    }

    public XMLStreamReaderToDomTreeTransformer() throws TransformerConfigurationException {
        this(TransformerFactoryImpl.newInstance().newTransformer());
    }

    public Node apply(final XMLStreamReader reader) {
        checkNotNull(reader, "XMLStreamReader must NOT be null");
        try {
            return toDomTree(reader);
        } catch (TransformerException e) {
            throw new RuntimeException("Failed to transform StAX stream into DOM tree.", e);
        }
    }

    private Node toDomTree(final XMLStreamReader reader) throws TransformerException {
        final DOMResult result = new DOMResult();
        transformer.transform(new StAXSource(reader), result);
        return result.getNode();
    }
}
