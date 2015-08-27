package com.carmatechnologies.utilities.xml.common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public final class InputStreams {

    private static final int SIZE_OF_GZIP_MAGIC_HEADER_IN_BYTES = 2;

    private InputStreams() {
        // Utility class, do NOT instantiate.
    }

    /**
     * Check if the provided instance of {@code InputStream} is GZipped, and:
     * - if so, transparently decorates it with {@code GZIPInputStream},
     * - if not, returns the stream as is.
     * As a result, the returned {@code InputStream} is guaranteed to be GUnzipped.
     * <p/>
     * WARNING: even if the stream is not GZipped, if it does not support "marking/resetting",
     * it will end up decorated with {@code BufferedInputStream}.
     *
     * @param in {@code InputStream} to automatically GUnzip.
     * @return the GUnzipped stream corresponding to the provided stream.
     * @throws IOException
     */
    public static InputStream autoGUnzip(InputStream in) throws IOException {
        // We need to read the first two bytes of the stream to know if it is GZipped or not.
        // As a consequence, if "marking" is not supported by the provided input stream, we wrap it with
        // BufferedInputStream which does support mark/reset, and allows us to read the first two bytes, and
        // "rewind", without corrupting the stream.
        if (!in.markSupported()) {
            in = new BufferedInputStream(in);
        }
        return (gzipMagicHeaderFor(in) == GZIPInputStream.GZIP_MAGIC) ? new GZIPInputStream(in) : in;
    }

    private static int gzipMagicHeaderFor(final InputStream in) throws IOException {
        in.mark(SIZE_OF_GZIP_MAGIC_HEADER_IN_BYTES);
        final int firstByte = in.read();
        final int secondByte = in.read();
        in.reset();

        return computeGZipMagicHeader(firstByte, secondByte);
    }

    /**
     * Compute GZip's "magic header" using the first two bytes of a potentially GZipped stream/file:
     * 0000 0000 0000 0000  0000 0000 ???? ???? |     <-- first read byte
     * 0000 0000 0000 0000  ???? ???? 0000 0000       <-- second read byte, shifted by one byte to the left
     * ---------------------------------------- ==?
     * 0000 0000 0000 0000  1000 1011 0001 1111       <-- {@code GZIPInputStream.GZIP_MAGIC}
     *
     * @param firstByte  first byte of the potentially GZipped stream/file
     * @param secondByte second byte of the potentially GZipped stream/file.
     * @return the resulting GZip "magic header".
     */
    private static int computeGZipMagicHeader(final int firstByte, final int secondByte) {
        return (firstByte & 0x000000FF) | ((secondByte << 8) & 0x0000FF00);
    }
}
