package com.carmatechnologies.utilities.xml.common;

import java.io.BufferedOutputStream;
import java.io.OutputStream;

public final class OutputStreams {
    private OutputStreams() {
        // Utility class, do NOT instantiate.
    }

    /**
     * Wraps the provided {@code OutputStream} in a {@code BufferedOutputStream} if not already done.
     *
     * @param in {@code OutputStream} to buffer.
     * @return {@code BufferedOutputStream} corresponding to the provided {@code OutputStream}.
     */
    public static OutputStream buffered(final OutputStream in) {
        return (in instanceof BufferedOutputStream)
                ? in
                : new BufferedOutputStream(in);
    }
}
