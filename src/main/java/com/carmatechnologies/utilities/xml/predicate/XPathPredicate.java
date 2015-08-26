package com.carmatechnologies.utilities.xml.predicate;

import com.google.common.base.Predicate;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public final class XPathPredicate implements Predicate<Node> {
    private final XPathExpression xpathExpression;

    public XPathPredicate(final XPathExpression xpathExpression) {
        this.xpathExpression = checkNotNull(xpathExpression, "XPathExpression must NOT be null.");
    }

    public XPathPredicate(final String xpathQuery) throws XPathExpressionException {
        this(XPathFactory.newInstance().newXPath().compile(checkNotNull(xpathQuery, "XPath query must NOT be null.")));
    }

    @Override
    public boolean apply(final Node domTree) {
        checkNotNull(domTree, "Node must NOT be null.");
        try {
            final String result = xpathExpression.evaluate(domTree);
            return !result.isEmpty();
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Failed to evaluate XPath expression on DOM tree.", e);
        }
    }
}
