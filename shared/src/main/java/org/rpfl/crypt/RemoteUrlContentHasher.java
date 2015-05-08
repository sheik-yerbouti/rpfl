package org.rpfl.crypt;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.net.HttpURLConnection.HTTP_OK;

@Singleton
public class RemoteUrlContentHasher {

    @Inject
    private InputStreamHasher inputStreamHasher;

    public byte[] hash(URL url) throws IOException {

        checkNotNull(url);

        switch(url.getProtocol()){
            case "http":
            case "https":
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                checkArgument(httpURLConnection.getResponseCode() == HTTP_OK);
                return inputStreamHasher.hash(httpURLConnection.getInputStream());
            case "ftp":
                return inputStreamHasher.hash(url.openStream());
            default:
                throw new IllegalArgumentException("remote url must be http, https or ftp");
        }
    }
}
