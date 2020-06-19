package com.carmatechnologies.utilities.xml.predicate;

import com.google.common.collect.Sets;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.util.function.Predicate;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class XPathSetPredicate extends AbstractXPathPredicate implements Predicate<Node> {
    private final Set<String> whiteList;

    public XPathSetPredicate(final XPathExpression xpathExpression, final Set<String> whiteList) {
        super(xpathExpression);
        this.whiteList = checkSet(whiteList);
    }

    public XPathSetPredicate(final String xpathQuery, final Set<String> whiteList) throws XPathExpressionException {
        super(xpathQuery);
        this.whiteList = checkSet(whiteList);
    }

    private Set<String> checkSet(final Set<String> whiteList) {
        checkNotNull(whiteList, "White-list set must NOT be null.");
        final Set<String> sanitizedWhiteList = sanitize(whiteList);
        checkArgument(!sanitizedWhiteList.isEmpty(), "White-list set must NOT be empty.");
        return sanitizedWhiteList;
    }

    private Set<String> sanitize(final Set<String> whiteList) {
        final Set<String> sanitizedWhiteList = Sets.newHashSetWithExpectedSize(whiteList.size());
        for (final String element : whiteList) {
            if (element == null)
                continue;
            final String sanitizedElement = element.trim();
            if (sanitizedElement.isEmpty())
                continue;
            sanitizedWhiteList.add(sanitizedElement);
        }
        return sanitizedWhiteList;
    }

    @Override
    protected boolean condition(final NodeList matchedNodes) {
        for (int i = 0; i < matchedNodes.getLength(); ++i) {
            final Node node = matchedNodes.item(i);
            final String text = node.getTextContent().trim();
            if (whiteList.contains(text))
                return true;
        }
        return false;
    }
}
