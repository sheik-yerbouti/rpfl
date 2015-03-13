package org.rpfl.crypt;

import java.security.SignatureException;

public interface EddsaSigner {
    byte[] sign(byte[] message) throws SignatureException;
}
