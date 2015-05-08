package org.rpfl.crypt;

public interface EddsaSigner {
    byte[] sign(byte[] message);
}
