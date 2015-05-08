package org.rpfl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static com.google.common.base.Preconditions.*;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.stream.Collectors.toList;
import static org.bouncycastle.util.Arrays.areEqual;

public class RpflImpl{

    private final Set<org.rpfl.TrustedEndpoint> trustedEndpoints;
    private final ExecutorService executorService = newCachedThreadPool();
    private final Cache<Set<URL>, byte[]> cache;
    private final SecureRandom random;

    public RpflImpl(Set<org.rpfl.TrustedEndpoint> trustedEndpoints) throws NoSuchAlgorithmException {
        checkNotNull(trustedEndpoints);
        checkArgument(!trustedEndpoints.isEmpty());

        this.trustedEndpoints = trustedEndpoints;

        random = SecureRandom.getInstanceStrong();

        cache = CacheBuilder
                        .newBuilder()
                        .expireAfterWrite(10, MINUTES)
                        .build();
    }

    public boolean validate(List<String> urlStringRepresentations, byte[] expectedHash) {

        Set<URL> urls = convert(urlStringRepresentations);

        checkNotNull(expectedHash);
        checkArgument(expectedHash.length == 64);

        //check if the hash for the url-set had been prepared.
        //we use synchronized here, to give prepareInternal-calls time to finish and
        //fill the cache before we check if the hash is cached
        synchronized (cache){
            Optional<byte[]> cachedHash = Optional.ofNullable(cache.getIfPresent(urls));

            if(cachedHash.isPresent()){
                return areEqual(cachedHash.get(), expectedHash);
            }
        }

        return getRandomTrustedEndpoints()//get a list of randomly picked trusted endpoints
                .parallelStream() // stream them in parallel
                .map(trustedEndpoint -> trustedEndpoint.isValid(urls, expectedHash))// ask all endpoints for validation of urls and expectedhash
                .allMatch(isValid -> isValid);//return true if all endpoints positively validated urls and expected hash, otherwise false
    }

    public void prepare(List<String> urlStringRepresentations) {
        checkNotNull(urlStringRepresentations);
        checkArgument(!urlStringRepresentations.isEmpty());

        Set<URL> urls = convert(urlStringRepresentations);

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

    private Set<TrustedEndpoint> getRandomTrustedEndpoints() {
        //a list of 1 to 5 trusted endpoints is returned
        //trusted endpoints are chosen randomly for each call, this reduces predictability for attackers

        int MAX_ENDPOINTS_TO_POLL = 5;

        Set<TrustedEndpoint> randomlyChosenEndpoints = newHashSet();

        if(trustedEndpoints.size() < MAX_ENDPOINTS_TO_POLL){
            randomlyChosenEndpoints.addAll(trustedEndpoints);
        } else {
            while(randomlyChosenEndpoints.size() < MAX_ENDPOINTS_TO_POLL){

                TrustedEndpoint randomEndpoint = get(trustedEndpoints, random.nextInt(randomlyChosenEndpoints.size() - 1));

                randomlyChosenEndpoints.add(randomEndpoint);
            }
        }

        return randomlyChosenEndpoints;
    }

    //dbus does not support neither Set nor URL, so we have to pass a list of String that is then converted
    private Set<URL> convert(List<String> urlStringRepresentations){
        checkNotNull(urlStringRepresentations);
        checkArgument(!urlStringRepresentations.isEmpty());

        Set<URL> urls = newHashSet();

        for (String urlStringRepresentation : urlStringRepresentations) {
            try {
                checkArgument(urls.add(new URL(urlStringRepresentation)));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        checkArgument(urlStringRepresentations.size() == urls.size(), "requested urls must be unique");

        return urls;
    }
}
