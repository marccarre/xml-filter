package com.carmatechnologies.utilities.xml.predicate;

import java.util.function.Predicate;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

public final class XPathPredicate extends AbstractXPathPredicate implements Predicate<Node> {

    public XPathPredicate(final XPathExpression xpathExpression) {
        super(xpathExpression);
    }

    public XPathPredicate(final String xpathQuery) throws XPathExpressionException {
        super(xpathQuery);
    }

    @Override
    protected boolean condition(final NodeList matchedNodes) {
        return matchedNodes.getLength() != 0;
    }
}
