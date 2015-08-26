package com.carmatechnologies.utilities.xml.predicate;

import com.google.common.base.Predicate;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Abstract parent class for XPath predicates, following the "Template Method" pattern:
 * - factorizes boilerplate, and
 * - defines {@link AbstractXPathPredicate#condition condition} as a method to implement in child classes.
 */
public abstract class AbstractXPathPredicate implements Predicate<Node> {
    private final XPathExpression xpathExpression;

    public AbstractXPathPredicate(final XPathExpression xpathExpression) {
        this.xpathExpression = checkNotNull(xpathExpression, "XPathExpression must NOT be null.");
    }

    public AbstractXPathPredicate(final String xpathQuery) throws XPathExpressionException {
        this(XPathFactory.newInstance().newXPath().compile(checkNotNull(xpathQuery, "XPath query must NOT be null.")));
    }

    @Override
    public boolean apply(final Node domTree) {
        checkNotNull(domTree, "Node must NOT be null.");
        try {
            final NodeList matchedNodes = (NodeList) xpathExpression.evaluate(domTree, XPathConstants.NODESET);
            return condition(matchedNodes);
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Failed to evaluate XPath expression on DOM tree.", e);
        }
    }

    protected abstract boolean condition(final NodeList matchedNodes);
}
