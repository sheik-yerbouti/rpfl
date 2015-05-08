package org.rpfl.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.rpfl.conf.Repository;
import org.rpfl.conf.RpflConfiguration;

import java.net.URL;
import java.util.Collection;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.io.Files.getFileExtension;

@Singleton
public class PackageUrlValidator {

    @Inject
    private RpflConfiguration rpflConfiguration;

    public void validate(Collection<URL> urls){
        checkNotNull(urls);
        checkArgument(areValid(urls));
    }

    public boolean areValid(Collection<URL> urls){
        return urls
            .parallelStream()
            .allMatch(this::isValid);
    }

    public void validate(URL url){
        checkArgument(isValid(url));
    }

    public boolean isValid(URL url){
        checkNotNull(url);

        Optional<Repository> repositoryOptional = rpflConfiguration
                .getRepositories()
                .stream()
                .filter(
                        repo ->
                                url.getProtocol().equals(repo.getUrl().getProtocol()) &&
                                        url.getHost().equals(repo.getUrl().getHost()) &&
                                        url.getPort() == repo.getUrl().getPort() &&
                                        url.getPath().startsWith(repo.getUrl().getPath())
                ).findFirst();

        if(!repositoryOptional.isPresent())
            return false;

        String extension = getFileExtension(url.getFile());

        return !isNullOrEmpty(extension) && repositoryOptional.get().getAllowedFileExtensions().contains(extension);

    }
}
