package org.rzlabs.halo.curator;

import org.apache.curator.framework.api.CompressionProvider;
import org.apache.curator.framework.imps.GzipCompressionProvider;

import java.io.IOException;

public class PotentiallyGzippedCompressionProvider implements CompressionProvider {

    private final boolean compressOutput;

    GzipCompressionProvider base = new GzipCompressionProvider();

    public PotentiallyGzippedCompressionProvider(boolean compressOutput) {
        this.compressOutput = compressOutput;
    }

    @Override
    public byte[] compress(String path, byte[] data) {
        return compressOutput ? base.compress(path, data) : data;
    }

    @Override
    public byte[] decompress(String path, byte[] data) {
        try {
            return base.decompress(path, data);
        } catch (IOException e) {
            return data;
        }
    }
}
