package org.rpfl.assembly;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.logging.log4j.Logger;
import org.rpfl.crypt.RemoteUrlContentHasher;
import org.rpfl.db.dao.ResourceFingerprintDao;
import org.rpfl.db.domain.ResourceFingerprint;

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toSet;
import static org.rpfl.transport.protobuf.Messages.VerificationStrength.downloaded;
import static org.rpfl.transport.protobuf.Messages.VerificationStrength.notfound;

@Singleton
public class Loader {

    @Inject
    private ResourceFingerprintDao resourceFingerprintDao;

    @Inject
    private ExecutorService executorService;

    @Inject
    private Logger logger;

    @Inject
    private RemoteUrlContentHasher remoteUrlContentHasher;

    public Set<ResourceFingerprint> loadFingerprints(Set<URL> urls) throws IOException {
        checkNotNull(urls);
        checkArgument(!urls.isEmpty());

        long start = currentTimeMillis();

        Set<ResourceFingerprint> resourceFingerprintSet = urls
                                                            .parallelStream()
                                                            .map(this::load)
                                                            .collect(toSet());

        long duration = currentTimeMillis() - start;

        logger.debug("fetching and digesting {} resources took {} miliseconds", urls.size(), duration);

        executorService.execute(() -> resourceFingerprintDao.save(resourceFingerprintSet));

        return resourceFingerprintSet;
    }

    private ResourceFingerprint load(URL url){
        checkNotNull(url);

        ResourceFingerprint resourceFingerprint = new ResourceFingerprint();
        resourceFingerprint.setUrl(url);

        try {
            resourceFingerprint.setHash(remoteUrlContentHasher.hash(url));
            resourceFingerprint.setVerificationStrength(downloaded);
        } catch (IOException e) {
            resourceFingerprint.setVerificationStrength(notfound);
            logger.warn("unable to load %s: %s", url, e.getMessage());
        }

        return resourceFingerprint;
    }
}
