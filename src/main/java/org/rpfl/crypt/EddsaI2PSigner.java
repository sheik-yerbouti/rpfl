package org.rpfl.crypt;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;

import java.security.InvalidKeyException;
import java.security.SignatureException;

import static com.google.common.base.Preconditions.checkNotNull;

public class EddsaI2PSigner implements EddsaSigner{

    private final ThreadLocal<EdDSAEngine> edDSAEngineThreadLocal;

    public EddsaI2PSigner(EdDSAPrivateKey edDSAPrivateKey) {
        checkNotNull(edDSAPrivateKey);

        edDSAEngineThreadLocal = new ThreadLocal<EdDSAEngine>(){
            @Override
            protected EdDSAEngine initialValue() {
                EdDSAEngine edDSAEngine = new EdDSAEngine();
                try {
                    edDSAEngine.initSign(edDSAPrivateKey);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                }

                return edDSAEngine;
            }
        };
    }

    @Override
    public byte[] sign(byte[] message) throws SignatureException {
        EdDSAEngine edDSAEngine = edDSAEngineThreadLocal.get();

        edDSAEngine.update(message);

        return edDSAEngine.sign();
    }
}
