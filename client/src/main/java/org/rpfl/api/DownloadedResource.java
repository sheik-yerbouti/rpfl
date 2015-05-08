package org.rpfl.api;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class DownloadedResource {
    private final Optional<Path> path;
    private final URL url;
    private byte[] hash;

    public DownloadedResource(byte[] hash, URL url){
        this.hash = checkNotNull(hash);
        this.url = checkNotNull(url);
        this.path = Optional.empty();
    }

    public DownloadedResource(Path path, URL url) {
        this.path = Optional.of(path);
        this.url = checkNotNull(url);
    }

    public byte[] getHash() throws NoSuchAlgorithmException, IOException {

        if(hash == null){

            checkState(path.isPresent());

            File file = path.get().toFile();

            checkState(file.exists());

            MessageDigest messageDigest = MessageDigest.getInstance("SHA3-512");

            byte[] buffer = new byte[4096];

            try(InputStream inputStream = new FileInputStream(file)){
                int read;

                do{
                    read = inputStream.read(buffer);
                    messageDigest.update(buffer);
                } while (read == buffer.length);
            }

            hash = messageDigest.digest();
        }

        return hash;
    }

    public URL getUrl() {
        return url;
    }
}
