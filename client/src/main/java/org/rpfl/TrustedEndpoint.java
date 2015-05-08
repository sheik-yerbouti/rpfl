package org.rpfl;

import org.rpfl.crypt.EddsaVerifier;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;
import static org.iq80.snappy.Snappy.compress;
import static org.rpfl.transport.protobuf.Messages.Request;

public class TrustedEndpoint {
    private final URL url;

    private final EddsaVerifier eddsaVerifier;

    public TrustedEndpoint(URL url, EddsaVerifier eddsaVerifier) {
        this.url = url;
        this.eddsaVerifier = eddsaVerifier;
    }

    public URL getUrl() {
        return url;
    }

    public boolean verify(Map<Path, URL> urlPathMap){

    }

    public boolean verify(Set<URL> urls, byte[] hash){
        List<String> urlStringRepresentations = urls
                                                    .stream()
                                                    .map(URL::toString)
                                                    .collect(toList());

        //request payload is snappy-compressed protocol buffers
        byte[] requestPayload = compress(
                Request
                        .newBuilder()
                        .addAllResources(urlStringRepresentations)
                        .build()
                        .toByteArray()
        );

        byte[] signature = new byte[64];

        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.getOutputStream().write(requestPayload);
            checkState(urlConnection.getInputStream().read(signature) == 64);
            checkState(urlConnection.getInputStream().available() == 0);
            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return eddsaVerifier.verify(hash, signature);
    }
}
