package org.rpfl.crypt;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.security.MessageDigest;

public class ThreadLocalMessageDigest extends ThreadLocal<MessageDigest>{

    @Override
    protected MessageDigest initialValue() {
        return new SHA3.DigestSHA3(512);
    }

    @Override
    public MessageDigest get() {
        MessageDigest messageDigest = super.get();

        messageDigest.reset();

        return messageDigest;
    }
}
