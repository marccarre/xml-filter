package com.carmatechnologies.utilities.xml;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * No-op stream filter, to use as a <a href="https://en.wikipedia.org/wiki/Null_Object_pattern">"Null Object"</a>.
 */
public final class NoOpStreamFilter implements StreamFilter {
    @Override
    public void filter(final InputStream in, final OutputStream out) {
        // Does nothing.
    }
}
