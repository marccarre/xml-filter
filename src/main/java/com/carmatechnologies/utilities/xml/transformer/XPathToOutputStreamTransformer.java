package com.carmatechnologies.utilities.xml.transformer;

import com.carmatechnologies.utilities.xml.common.Pair;
import com.google.common.base.Function;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.OutputStream;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;

public final class XPathToOutputStreamTransformer implements Function<Pair<Node, OutputStream>, Void> {
    private final XPathExpression xpathExpression;

    public XPathToOutputStreamTransformer(final XPathExpression xpathExpression) {
        this.xpathExpression = checkNotNull(xpathExpression, "XPathExpression must NOT be null.");
    }

    public XPathToOutputStreamTransformer(final String xpathQuery) throws XPathExpressionException {
        this(XPathFactory.newInstance().newXPath().compile(checkNotNull(xpathQuery, "XPath query must NOT be null.")));
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
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Failed to evaluate XPath expression on DOM tree.", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write XPath result to output stream.", e);
        }
    }

    private void toOutputStream(final Node domTree, final OutputStream out) throws XPathExpressionException, IOException {
        final String result = xpathExpression.evaluate(domTree);
        if (!result.isEmpty()) {
            out.write((result + "\n").getBytes(UTF_8));
        }
    }
}
