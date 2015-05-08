package org.rpfl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.rpfl.api.DownloadedResource;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.bouncycastle.util.Arrays.areEqual;

public class Rpfl {

    private final Set<org.rpfl.TrustedEndpoint> trustedEndpoints;
    private final ExecutorService executorService = newCachedThreadPool();
    private final Cache<Set<URL>, byte[]> cache;
    private final SecureRandom random;

    public Rpfl(Set<org.rpfl.TrustedEndpoint> trustedEndpoints) throws NoSuchAlgorithmException {
        checkNotNull(trustedEndpoints);
        checkArgument(!trustedEndpoints.isEmpty());

        this.trustedEndpoints = trustedEndpoints;

        random = SecureRandom.getInstanceStrong();

        cache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(120, MINUTES)
                .expireAfterAccess(0, SECONDS)
                .build();
    }


    public void prepare(Set<URL> urls){
        //return immediately and prepare in background
        executorService.execute(()-> prepareInternal(urls));
    }

    private void prepareInternal(Set<URL> urls){

        synchronized (cache){

            //get randomly chosen list of trusted endpoints and
            //ask all endpoints for hashes of the url-set
            List<byte[]> hashes = getRandomTrustedEndpoints()
                    .parallelStream()
                    .map(trustedEndpoint -> trustedEndpoint.getHash(urls))
                    .collect(toList());

            //pick the first hash in the list to be the reference-hash
            byte[] referenceHash = hashes.get(0);

            //assert that all trusted endpoints returned the same hash
            boolean allHashesSame = hashes
                    .stream()
                    .allMatch(hash -> areEqual(hash, referenceHash));

            checkState(allHashesSame);

            //cache the url/hash-tuple
            cache.put(urls, referenceHash);
        }
    }
    }

    public boolean verify(Set<DownloadedResource> downloadedResources){

    }
}
