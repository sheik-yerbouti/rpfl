package org.rpfl.crypt;

import com.google.inject.Singleton;

import java.io.ByteArrayOutputStream;

@Singleton
public class ThreadLocalByteArrayOutputStream extends ThreadLocal<ByteArrayOutputStream>
{
    public static final int SIZE = 2048;

    @Override
    protected ByteArrayOutputStream initialValue()
    {
        return new ByteArrayOutputStream(SIZE);
    }

    @Override
    public ByteArrayOutputStream get()
    {
        ByteArrayOutputStream byteArrayOutputStream = super.get();

        byteArrayOutputStream.reset();

        return byteArrayOutputStream;
    }
}
