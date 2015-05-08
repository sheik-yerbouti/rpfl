package org.rpfl.crypt;

import org.abstractj.kalium.keys.SigningKey;

public class EddsaKaliumSigner implements EddsaSigner {

    private final SigningKey signingKey;

    public EddsaKaliumSigner(SigningKey signingKey) {
        this.signingKey = signingKey;
    }

    public byte[] sign(byte[] message){
        return signingKey.sign(message);
    }
}
