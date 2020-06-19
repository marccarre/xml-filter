package com.carmatechnologies.utilities.xml.predicate;

import com.google.common.collect.Sets;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;
import java.util.Set;

import static com.carmatechnologies.utilities.xml.TestingUtilities.parseDomTree;
import static com.carmatechnologies.utilities.xml.TestingUtilities.streamFor;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class XPathSetPredicateTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final Node domTree = parseDomTree(streamFor("/books.xml"));

    @Test
    public void xpathSetPredicateShouldReturnTrueWhenXPathQueryFindsElementsFromSet() throws XPathExpressionException {
        Set<String> whiteList = Sets.newHashSet("magician", "xml");
        assertThat(new XPathSetPredicate("//book/tags/tag/text()", whiteList).test(domTree), is(true));
        assertThat(new XPathSetPredicate("//book/descendant::*/text()", whiteList).test(domTree), is(true));
    }

    @Test
    public void xpathSetPredicateShouldReturnFalseWhenXPathQueryFindsElementsButTheseAreNotInSet() throws XPathExpressionException {
        Set<String> badWhiteList = Sets.newHashSet("non-existant", "not here!");
        assertThat(new XPathSetPredicate("//book/tags/tag/text()", badWhiteList).test(domTree), is(false));
        assertThat(new XPathSetPredicate("//book/descendant::*/text()", badWhiteList).test(domTree), is(false));
    }

    @Test
    public void xpathSetPredicateShouldReturnFalseWhenXPathQueryDoesNotFindAnyElement() throws XPathExpressionException {
        Set<String> whiteList = Sets.newHashSet("magician", "xml");
        assertThat(new XPathSetPredicate("//non-existant/text()", whiteList).test(domTree), is(false));
        assertThat(new XPathSetPredicate("//non-existant/descendant::*/text()", whiteList).test(domTree), is(false));
    }

    @Test
    public void xpathSetPredicateShouldBeReusable() throws XPathExpressionException {
        Set<String> whiteList = Sets.newHashSet("magician", "xml");
        final XPathSetPredicate predicate = new XPathSetPredicate("//book/tags/tag/text()", whiteList);

        assertThat(predicate.test(domTree), is(true));
        assertThat(predicate.test(domTree), is(true)); // Evaluate another time...
        assertThat(predicate.test(domTree), is(true)); // Evaluate yet another time...
    }

    @Test
    public void xpathSetPredicateShouldSanitizeInputAndReturnTrueWhenXPathQueryFindsElementsFromSet() throws XPathExpressionException {
        Set<String> whiteList = Sets.newHashSet("  magician  ", "     xml    ", null, "", "   ");
        assertThat(new XPathSetPredicate("//book/tags/tag/text()", whiteList).test(domTree), is(true));
        assertThat(new XPathSetPredicate("//book/descendant::*/text()", whiteList).test(domTree), is(true));
    }

    @Test
    public void xpathSetPredicateShouldSanitizeInputAndThrowIllegalArgumentExceptionIfSetIsEmptyAfterSanitization() throws XPathExpressionException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(equalTo("White-list set must NOT be empty."));

        Set<String> whiteList = Sets.newHashSet("  ", null, "", "      ");
        new XPathSetPredicate("//book/tags/tag/text()", whiteList);
    }
}
