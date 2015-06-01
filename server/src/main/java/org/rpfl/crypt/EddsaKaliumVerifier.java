package org.rpfl.crypt;

import org.abstractj.kalium.keys.VerifyKey;

public class EddsaKaliumVerifier implements EddsaVerifier
{
    private final VerifyKey verifyKey;

    public EddsaKaliumVerifier(VerifyKey verifyKey) {
        this.verifyKey = verifyKey;
    }

    @Override
    public boolean verify(byte[] message, byte[] signature) {
        return verifyKey.verify(message, signature);
    }
}
