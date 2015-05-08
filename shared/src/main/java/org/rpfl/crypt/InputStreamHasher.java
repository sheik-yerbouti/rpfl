package org.rpfl.crypt;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class InputStreamHasher {

    @Inject
    private ThreadLocalMessageDigest threadLocalMessageDigest;

    @Inject
    private ThreadLocalBuffer threadLocalBuffer;

    public byte[] hash(InputStream inputStream) throws IOException {

        checkNotNull(inputStream);

        MessageDigest messageDigest = threadLocalMessageDigest.get();

        byte[] buffer = threadLocalBuffer.get();

        try(InputStream closeableInputStream = inputStream){
            int read;
            do {
                read = closeableInputStream.read(buffer);
                messageDigest.update(buffer, 0, read);
            } while (read == buffer.length);
        }

        return checkNotNull(messageDigest.digest());
    }
}
