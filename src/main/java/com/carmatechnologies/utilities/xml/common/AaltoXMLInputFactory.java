package com.carmatechnologies.utilities.xml.common;

import com.fasterxml.aalto.stax.InputFactoryImpl;

import javax.xml.stream.XMLInputFactory;

public final class AaltoXMLInputFactory {
    private AaltoXMLInputFactory() {
        // Utility class, do NOT instantiate.
    }

    public static XMLInputFactory newInstance() {
        final InputFactoryImpl inputFactory = new InputFactoryImpl();
        inputFactory.configureForSpeed();
        return inputFactory;
    }
}
