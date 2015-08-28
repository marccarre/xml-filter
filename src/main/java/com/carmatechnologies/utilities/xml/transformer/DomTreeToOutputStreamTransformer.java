package com.carmatechnologies.utilities.xml.transformer;

import com.carmatechnologies.utilities.xml.common.Pair;
import com.carmatechnologies.utilities.xml.common.TransformerFactoryImpl;
import com.google.common.base.Function;
import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;

import static com.google.common.base.Preconditions.checkNotNull;

public final class DomTreeToOutputStreamTransformer implements Function<Pair<Node, OutputStream>, Void> {
    private final Transformer transformer;

    public DomTreeToOutputStreamTransformer(final Transformer transformer) {
        this.transformer = checkNotNull(transformer, "Transformer must NOT be null.");
    }

    public DomTreeToOutputStreamTransformer() throws TransformerConfigurationException {
        this(TransformerFactoryImpl.newInstance().newTransformer());
    }

    @Override
    public Void apply(final Pair<Node, OutputStream> pair) {
        checkNotNull(pair, "Pair<Node, OutputStream> must NOT be null.");
        final Node domTree = pair.first();
        checkNotNull(domTree, "Node must NOT be null.");
        final OutputStream out = pair.second();
        checkNotNull(out, "OutputStream must NOT be null.");

        try {
            toOutputStream(domTree, out);
            return null;
        } catch (TransformerException e) {
            throw new RuntimeException("Failed to write DOM tree to output stream.", e);
        }
    }

    private void toOutputStream(final Node domTree, final OutputStream out) throws TransformerException {
        transformer.transform(new DOMSource(domTree), new StreamResult(out));
    }
}
