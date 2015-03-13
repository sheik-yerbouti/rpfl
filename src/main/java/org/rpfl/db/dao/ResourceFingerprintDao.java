package org.rpfl.db.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import org.rpfl.db.domain.ResourceFingerprint;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.copyOf;
import static org.rpfl.transport.protobuf.Messages.VerificationStrength.downloaded;

@Singleton
public class ResourceFingerprintDao {

    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public Set<ResourceFingerprint> getResourceFingerprints(Set<URL> urls) {
        checkNotNull(urls);

        return copyOf(
            entityManagerProvider
                .get()
                .createQuery(
                    "from ResourceFingerprint where url in :urls",
                    ResourceFingerprint.class
                )
                .setParameter("urls", urls)
                .getResultList()
        );
    }

    @Transactional
    public void save(Set<ResourceFingerprint> resourceFingerprints) {
        checkNotNull(resourceFingerprints);

        EntityManager entityManager = entityManagerProvider.get();

        resourceFingerprints
                .stream()
                .filter(fingerprint -> downloaded.equals(fingerprint.getVerificationStrength()))
                .forEach(entityManager::persist);
    }
}
