package com.carmatechnologies.utilities.xml.common;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class MutablePairTest {

    @Test
    public void ofShouldFullyInitializePair() {
        Pair<String, Integer> pair = MutablePair.of("a", 1);
        assertThat(pair, is(not(nullValue())));
        assertThat(pair.first(), is("a"));
        assertThat(pair.second(), is(1));
    }

    @Test
    public void mutablePairCanHaveItsElementsModified() {
        MutablePair<String, Integer> pair = MutablePair.of("a", 1);
        assertThat(pair.first(), is("a"));
        assertThat(pair.second(), is(1));

        pair.first("b");
        assertThat(pair.first(), is("b"));

        pair.second(2);
        assertThat(pair.second(), is(2));
    }

    @Test
    public void withFirstShouldPartiallyInitializePairWithFirstElement() {
        Pair<String, Integer> pair = MutablePair.withFirst("a");
        assertThat(pair, is(not(nullValue())));
        assertThat(pair.first(), is("a"));
        assertThat(pair.second(), is(nullValue()));
    }

    @Test
    public void withSecondShouldPartiallyInitializePairWithSecondElement() {
        Pair<String, Integer> pair = MutablePair.withSecond(1);
        assertThat(pair, is(not(nullValue())));
        assertThat(pair.first(), is(nullValue()));
        assertThat(pair.second(), is(1));
    }

}
