package com.carmatechnologies.utilities.xml.transformer;

import com.google.common.base.Function;
import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;

import static com.google.common.base.Preconditions.checkNotNull;

public final class DomTreeToOutputStreamTransformer implements Function<Node, Void> {
    private final OutputStream out;
    private final Transformer transformer;

    public DomTreeToOutputStreamTransformer(final OutputStream out, final Transformer transformer) {
        this.out = checkNotNull(out, "OutputStream must NOT be null.");
        this.transformer = checkNotNull(transformer, "Transformer must NOT be null.");
    }

    public DomTreeToOutputStreamTransformer(final OutputStream out) throws TransformerConfigurationException {
        this(out, TransformerFactory.newInstance().newTransformer());
    }

    @Override
    public Void apply(final Node domTree) {
        checkNotNull(domTree, "Node must NOT be null.");
        try {
            toOutputStream(domTree);
            return null;
        } catch (TransformerException e) {
            throw new RuntimeException("Failed to write DOM tree to output stream.", e);
        }
    }

    private void toOutputStream(final Node domTree) throws TransformerException {
        transformer.transform(new DOMSource(domTree), new StreamResult(out));
    }
}
