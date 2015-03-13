package org.rpfl.assembly.common;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.logging.log4j.Logger;
import org.rpfl.crypt.InputStreamHash;
import org.rpfl.db.dao.ResourceFingerprintDao;
import org.rpfl.db.domain.ResourceFingerprint;
import org.rpfl.crypt.FileContentHasher;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toSet;
import static org.rpfl.transport.protobuf.Messages.VerificationStrength;
import static org.rpfl.transport.protobuf.Messages.VerificationStrength.recompiled;

@Singleton
public class LocalBuildSynchronizer {

    @Inject
    private FileContentHasher fileContentHasher;

    @Inject
    @Named("localBuildDirectory")
    private Optional<Path> localBuildDirectory;

    @Inject
    private ResourceFingerprintDao resourceFingerprintDao;

    @Inject
    private Logger logger;

    @Inject
    private ExecutorService executorService;

    private final String remoteUrlStringRepresentation;

    @Inject
    public LocalBuildSynchronizer(@Named("repositoryUrl") URL remoteURL) {
        this.remoteUrlStringRepresentation = remoteURL.toString();
    }

    public void synchronize(Set<ResourceFingerprint> resourceFingerprints) throws IOException, SignatureException {
        if(!localBuildDirectory.isPresent()){
            return;
        }

        checkNotNull(resourceFingerprints);

        Set<ResourceFingerprint> downloaded = resourceFingerprints
                .stream()
                .filter(resourceFingerprint -> VerificationStrength.downloaded.equals(resourceFingerprint.getVerificationStrength()))
                .collect(toSet());

        boolean hit = false;

        for (ResourceFingerprint downloadedFingerprint : downloaded) {
            String urlStringRepresentation = downloadedFingerprint.getUrl().toString();

            checkArgument(urlStringRepresentation.startsWith(remoteUrlStringRepresentation));

            String urlStringEndPath = urlStringRepresentation.substring(remoteUrlStringRepresentation.length());

            Path path = Paths.get(localBuildDirectory.get().toString(), urlStringEndPath);

            if(!path.toFile().exists()){
                continue;
            }

            InputStreamHash inputStreamHash = fileContentHasher.takeHash(path);

            if(downloadedFingerprint.getSize() != inputStreamHash.getContentSize()){
                logger.error("downloaded and recompiled size does not match for URL {}", urlStringRepresentation);
                continue;
            }

            if(!Arrays.equals(downloadedFingerprint.getHash(), inputStreamHash.getSHA3_384())){
                logger.error("downloaded and recompiled hash does not match for URL {}", urlStringRepresentation);
                continue;
            }

            downloadedFingerprint.setVerificationStrength(recompiled);

            hit = true;
        }

        if(hit){
            executorService.execute(()->resourceFingerprintDao.save(downloaded));
        }
    }
}
