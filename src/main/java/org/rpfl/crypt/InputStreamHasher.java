package org.rpfl.crypt;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.SignatureException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class InputStreamHasher {

    @Inject
    private ThreadLocalMessageDigest threadLocalMessageDigest;

    @Inject
    private ThreadLocalBuffer threadLocalBuffer;

    public InputStreamHash getVerificationData(InputStream inputStream) throws IOException, SignatureException {

        checkNotNull(inputStream);

        MessageDigest messageDigest = threadLocalMessageDigest.get();

        byte[] buffer = threadLocalBuffer.get();

        long size = 0;

        try(InputStream closeableInputStream = inputStream){
            int read;

            do {
                read = closeableInputStream.read(buffer);
                messageDigest.update(buffer, 0, read);
                size += read;
            } while (read == buffer.length);
        }

        checkArgument(size > 0);

        byte[] hash = checkNotNull(messageDigest.digest());

        return new InputStreamHash(size, hash);
    }
}
