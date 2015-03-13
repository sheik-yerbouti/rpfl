package org.rpfl.crypt;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.SignatureException;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

@Singleton
public class FileContentHasher {

    @Inject
    private InputStreamHasher inputStreamHasher;

    @Inject
    @Named("localBuildDirectory")
    private Optional<Path> localBuildDirectory;

    public InputStreamHash takeHash(Path path) throws IOException, SignatureException {
        checkState(localBuildDirectory.isPresent(), "for security reasons, FileContentHasher cannot be used if localBuildDirectory is not supplied");

        File file = path.toFile();

        checkArgument(path.startsWith(localBuildDirectory.get()), "path must be located below local build-directory");
        checkArgument(path.isAbsolute(), "path must be absolute");

        return inputStreamHasher.getVerificationData(new FileInputStream(file));
    }
}
