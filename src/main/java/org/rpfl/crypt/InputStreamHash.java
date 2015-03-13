package org.rpfl.crypt;

public class InputStreamHash {

    private final long size;
    private final byte[] hash;

    public InputStreamHash(long size, byte[] hash) {
        this.size = size;
        this.hash = hash;
    }

    public long getContentSize() {
        return size;
    }

    public byte[] getSHA3_384() {
        return hash;
    }
}
