package com.carmatechnologies.utilities.xml;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface to represent a filter in a "Pipe & Filter" architecture where pipes are {@code InputStream}s and {@code OutputStream}s.
 * See also:
 * - <a href="https://en.wikipedia.org/wiki/Pipeline_(software)">https://en.wikipedia.org/wiki/Pipeline_(software)</a>
 * - <a href="https://msdn.microsoft.com/en-us/library/ff647419.aspx">https://msdn.microsoft.com/en-us/library/ff647419.aspx</a>
 */
public interface StreamFilter {
    void filter(final InputStream in, final OutputStream out) throws XMLStreamException;
}
