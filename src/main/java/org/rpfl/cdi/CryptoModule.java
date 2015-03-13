package org.rpfl.cdi;

import com.google.inject.AbstractModule;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;
import org.abstractj.kalium.NaCl;
import org.abstractj.kalium.keys.SigningKey;
import org.rpfl.crypt.EddsaI2PSigner;
import org.rpfl.crypt.EddsaKaliumSigner;
import org.rpfl.crypt.EddsaSigner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.io.Files.readFirstLine;
import static java.lang.System.err;
import static org.bouncycastle.util.encoders.Hex.decode;
import static org.bouncycastle.util.encoders.Hex.toHexString;

public class CryptoModule extends AbstractModule {

    private String fileName;

    public CryptoModule(String fileName) {
        this.fileName = fileName;
    }

    protected void configure() {

        Path path = Paths.get(fileName);

        File file = path.toFile();

        byte[] privateKey;

        try{
            if(file.exists()){
                privateKey = decode(readFirstLine(file, Charset.forName("UTF-8")));

                if(privateKey.length != 32){
                    file.delete();
                    configure();
                    return;
                }
            } else {
                privateKey = new byte[32];

                SecureRandom.getInstanceStrong().nextBytes(privateKey);

                try(OutputStream outputStream = new FileOutputStream(file)){
                    outputStream.write(toHexString(privateKey).getBytes("UTF-8"));
                }
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }

        EddsaSigner eddsaSigner;

        try{
            checkState(NaCl.init() != -1);

            err.println("NaCl initialized properly, using native code for signatures");

            SigningKey signingKey = new SigningKey(privateKey);

            err.println("public key is " + toHexString(signingKey.getVerifyKey().toBytes()));

            eddsaSigner = new EddsaKaliumSigner(signingKey);
        } catch(Throwable t){
            err.println("unable to load NaCl, falling back to java implementation for signatures");

            EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName("ed25519-sha-512");
            EdDSAPrivateKeySpec privateKeySpec = new EdDSAPrivateKeySpec(privateKey, spec);
            EdDSAPublicKeySpec pubKeySpec = new EdDSAPublicKeySpec(privateKeySpec.getA(), spec);
            EdDSAPublicKey publicKey = new EdDSAPublicKey(pubKeySpec);

            err.println("public key is " + toHexString(publicKey.getAbyte()));

            EdDSAPrivateKey eddsaPrivateKey = new EdDSAPrivateKey(privateKeySpec);

            eddsaSigner = new EddsaI2PSigner(eddsaPrivateKey);
        }

        bind(EddsaSigner.class).toInstance(eddsaSigner);
    }
}
