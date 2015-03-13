package org.rpfl.crypt;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SignatureException;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.io.Files.getFileExtension;
import static java.net.HttpURLConnection.HTTP_OK;

@Singleton
public class RemoteUrlContentHasher {

    @Inject
    private InputStreamHasher inputStreamHasher;

    @Inject
    @Named("repositoryUrl")
    private URL repositoryUrl;

    @Inject
    @Named("allowedFileExtensions")
    private Set<String> allowedFileExtensions;

    public InputStreamHash hash(URL url) throws IOException, SignatureException {
        checkNotNull(url);

        checkArgument(repositoryUrl.getProtocol().equals(url.getProtocol()));
        checkArgument(repositoryUrl.getHost().equals(url.getHost()));
        checkArgument(repositoryUrl.getPort() == url.getPort());
        checkArgument(url.getPath().startsWith(repositoryUrl.getPath()));
        checkArgument(isNullOrEmpty(repositoryUrl.getQuery()));

        String extension = checkNotNull(getFileExtension(url.getFile()));

        checkArgument(!extension.isEmpty(), "files without file-extensions are not allowed");
        checkArgument(allowedFileExtensions.contains(extension), "file-type %s is not allowed", extension);

        InputStream inputStream;

        if(isHttp(url)){
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

            checkArgument(httpURLConnection.getResponseCode() == HTTP_OK);

            inputStream = httpURLConnection.getInputStream();
        } else {
            inputStream = url.openStream();
        }

        return inputStreamHasher.getVerificationData(inputStream);
    }

    private boolean isHttp(URL url){
        return url.getProtocol().startsWith("http");
    }
}
