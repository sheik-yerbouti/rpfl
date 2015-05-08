package org.rpfl.assembly;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.rpfl.db.dao.ResourceFingerprintDao;
import org.rpfl.db.domain.ResourceFingerprint;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.union;
import static java.util.stream.Collectors.toSet;

@Singleton
public class FingerprintProvider {

    @Inject
    private ResourceFingerprintDao resourceFingerprintDao;

    @Inject
    private Loader loader;

    public Set<ResourceFingerprint> get(Set<URL> requestedUrls) throws IOException{
        checkNotNull(requestedUrls);

        Set<ResourceFingerprint> resourceFingerprints = resourceFingerprintDao.getResourceFingerprints(requestedUrls);

        boolean allFound = requestedUrls.size() == resourceFingerprints.size();

        if (!allFound) {
            Set<URL> foundUrls = resourceFingerprints
                                    .stream()
                                    .map(ResourceFingerprint::getUrl)
                                    .collect(toSet());

            Set<URL> missingUrls = difference(requestedUrls, foundUrls);

            Set<ResourceFingerprint> fetchedResourceFingerprints = loader.loadFingerprints(missingUrls);

            resourceFingerprints = union(resourceFingerprints, fetchedResourceFingerprints);
        }

        return resourceFingerprints;
    }
}
