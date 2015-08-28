package com.carmatechnologies.utilities.xml.common;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.carmatechnologies.utilities.xml.TestingUtilities.streamFor;
import static com.carmatechnologies.utilities.xml.TestingUtilities.toUtf8String;
import static com.google.common.base.Charsets.UTF_8;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class InputStreamsTest {

    private static final String HELLO_WORLD = "Hello World\n";

    @Test
    public void autoGUnzippedStreamForTextFileShouldBeReadableInClear() throws IOException {
        InputStream textStream = streamFor("/sample_text.txt");
        assertThat(toUtf8String(textStream), is(HELLO_WORLD));

        InputStream in = InputStreams.autoGUnzip(streamFor("/sample_text.txt"));
        assertThat(toUtf8String(in), is(HELLO_WORLD));
    }

    @Test
    public void autoGUnzippedStreamForGZippedFileShouldBeReadableInClear() throws IOException {
        InputStream gzippedStream = streamFor("/sample_text.txt.gz");
        assertThat(toUtf8String(gzippedStream), is(not(HELLO_WORLD)));

        InputStream in = InputStreams.autoGUnzip(streamFor("/sample_text.txt.gz"));
        assertThat(toUtf8String(in), is(HELLO_WORLD));
    }

    @Test
    public void autoGUnzippedNonMarkableStreamShouldBeReadableInClear() throws IOException {
        InputStream nonMarkableStream = new NonMarkableByteArrayInputStream(HELLO_WORLD.getBytes(UTF_8));
        assertThat(toUtf8String(InputStreams.autoGUnzip(nonMarkableStream)), is(HELLO_WORLD));
    }

    private static class NonMarkableByteArrayInputStream extends ByteArrayInputStream {
        public NonMarkableByteArrayInputStream(final byte[] buf) {
            super(buf);
        }

        @Override
        public boolean markSupported() {
            return false;
        }

        @Override
        public synchronized void mark(final int position) {
            throw new RuntimeException("mark/reset not supported");
        }

        @Override
        public synchronized void reset() {
            throw new RuntimeException("mark/reset not supported");
        }
    }

    @Test
    public void bufferedShouldWrapTheProvidedInputStreamInBufferedInputStream() {
        InputStream in = new ByteArrayInputStream(HELLO_WORLD.getBytes(UTF_8));
        assertThat(in, is(not(instanceOf(BufferedInputStream.class))));
        assertThat(InputStreams.buffered(in), is(instanceOf(BufferedInputStream.class)));
    }

}
