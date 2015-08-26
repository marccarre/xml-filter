package com.carmatechnologies.utilities.xml.predicate;

import org.junit.Test;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;

import static com.carmatechnologies.utilities.xml.TestingUtilities.parseDomTree;
import static com.carmatechnologies.utilities.xml.TestingUtilities.streamFor;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class XPathPredicateTest {

    private final Node domTree = parseDomTree(streamFor("/books.xml"));

    @Test
    public void xpathPredicateShouldReturnTrueWhenXPathQueryFindsElements() throws XPathExpressionException {
        assertThat(new XPathPredicate("//book/tags/tag[text() = 'magician']").apply(domTree), is(true));
        assertThat(new XPathPredicate("//book/descendant::*[text() = 'magician']").apply(domTree), is(true));
    }

    @Test
    public void xpathPredicateShouldReturnFalseWhenXPathQueryDoesNotFindAnyElement() throws XPathExpressionException {
        assertThat(new XPathPredicate("//book/tags/tag[text() = 'non-existant']").apply(domTree), is(false));
        assertThat(new XPathPredicate("//book/descendant::*[text() = 'non-existant']").apply(domTree), is(false));
    }

    @Test
    public void xpathPredicateShouldBeReusable() throws XPathExpressionException {
        XPathPredicate predicate = new XPathPredicate("//book/tags/tag[text() = 'magician']");

        assertThat(predicate.apply(domTree), is(true));
        assertThat(predicate.apply(domTree), is(true)); // Evaluate another time...
        assertThat(predicate.apply(domTree), is(true)); // Evaluate yet another time...
    }

}
