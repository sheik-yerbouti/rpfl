package org.rpfl.crypt;

import com.google.inject.Singleton;

@Singleton
class ThreadLocalBuffer extends ThreadLocal<byte[]> {
    private static final int BUFFER_SIZE = 4096;

    @Override
    protected byte[] initialValue() {
        return new byte[BUFFER_SIZE];
    }
}
