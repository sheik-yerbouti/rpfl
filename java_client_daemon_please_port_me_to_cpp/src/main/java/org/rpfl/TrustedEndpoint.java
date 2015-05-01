package org.rpfl;

import com.google.common.base.VerifyException;
import org.abstractj.kalium.keys.VerifyKey;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;
import static org.bouncycastle.util.Arrays.areEqual;
import static org.iq80.snappy.Snappy.compress;
import static org.rpfl.transport.protobuf.Messages.Request;

public class TrustedEndpoint {
    private final URL url;
    private final VerifyKey publicKey;

    public TrustedEndpoint(URL url, VerifyKey publikKey) {
        this.url = url;
        this.publicKey = publikKey;
    }

    public URL getUrl() {
        return url;
    }

    public byte[] getHash(Set<URL> urls){
        List<String> urlStringRepresentations = urls
                                                    .stream()
                                                    .map(URL::toString)
                                                    .collect(toList());

        //request payload is snappy-compressed protocol buffer
        byte[] requestPayload = compress(
                Request
                        .newBuilder()
                        .addAllResources(urlStringRepresentations)
                        .setFullResponse(false)
                        .build()
                        .toByteArray()
        );

        byte[] hash = new byte[64];
        byte[] signature = new byte[64];

        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.getOutputStream().write(requestPayload);

            //first 64 bytes of the response is the hash value, second 64 bytes is an ed25519-signature of the hash
            checkState(urlConnection.getInputStream().read(hash) == 64);
            checkState(urlConnection.getInputStream().read(signature) == 64);
            checkState(urlConnection.getInputStream().available() == 0);

            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!publicKey.verify(hash, signature)){
            throw new VerifyException();
        }

        return hash;
    }

    public boolean isValid(Set<URL> urls, byte[] expectedHash){
        return areEqual(getHash(urls), expectedHash);
    }
}
