package org.rpfl.crypt;

public interface EddsaVerifier {
    boolean verify(byte[] message, byte[] signature);
}
