package com.carmatechnologies.utilities.xml.common;

import javax.xml.transform.TransformerFactory;

public final class TransformerFactoryImpl {
    private TransformerFactoryImpl() {
        // Utility class, do NOT instantiate.
    }

    public static TransformerFactory newInstance() {
        return TransformerFactory.newInstance();
    }
}
