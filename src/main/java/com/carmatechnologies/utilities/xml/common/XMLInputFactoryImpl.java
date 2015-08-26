package com.carmatechnologies.utilities.xml.common;

import com.ctc.wstx.stax.WstxInputFactory;

import javax.xml.stream.XMLInputFactory;

public final class XMLInputFactoryImpl {
    private XMLInputFactoryImpl() {
        // Utility class, do NOT instantiate.
    }

    public static XMLInputFactory newInstance() {
        return WstxInputFactory.newInstance();
    }
}
