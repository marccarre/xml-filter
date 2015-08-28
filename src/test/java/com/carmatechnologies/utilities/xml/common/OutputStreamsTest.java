package com.carmatechnologies.utilities.xml.common;

import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class OutputStreamsTest {

    @Test
    public void bufferedShouldWrapTheProvidedOutputStreamInBufferedOutputStream() {
        OutputStream in = new ByteArrayOutputStream();
        assertThat(in, is(not(instanceOf(BufferedOutputStream.class))));
        assertThat(OutputStreams.buffered(in), is(instanceOf(BufferedOutputStream.class)));
    }

}
