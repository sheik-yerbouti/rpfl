package org.rpfl.test;

import org.junit.Test;

import java.net.MalformedURLException;

public class ResourceFingerprintDaoTest {
    @Test
    public void test_positive_simple() throws MalformedURLException {
        /*
        Injector injector = createInjector(new JpaPersistModule("rpfl"));
        ResourceFingerprintDao fingerprintDao = injector.getInstance(ResourceFingerprintDao.class);
        injector.getInstance(PersistService.class).start();

        URL url1 = new URL("http://www.google.de");
        ResourceFingerprint resourceFingerprint1 = new ResourceFingerprint(url1, new byte[32], Messages.VerificationStrength.downloaded, hash, size);
        URL url2 = new URL("http://www.yahoo.com");
        ResourceFingerprint resourceFingerprint2 = new ResourceFingerprint(url2, new byte[32], Messages.VerificationStrength.downloaded, hash, size);
        URL url3 = new URL("http://www.oracle.com");
        ResourceFingerprint resourceFingerprint3 = new ResourceFingerprint(url3, new byte[32], Messages.VerificationStrength.downloaded, hash, size);
        fingerprintDao.save(ImmutableSet.of(resourceFingerprint1, resourceFingerprint2, resourceFingerprint3));
        Set<ResourceFingerprint> resourceFingerprints = fingerprintDao.getResourceFingerprints(ImmutableSet.of(url1, url2));
        assertThat(resourceFingerprints.size(), is(2));

        assertEquals(1, resourceFingerprints.stream().filter(rfp -> rfp.getUrl().equals(url1)).count());
        assertEquals(1, resourceFingerprints.stream().filter(rfp -> rfp.getUrl().equals(url2)).count());
        */
    }

    //@Test(expected=EntityExistsException.class)
    public void test_negative_simple() throws MalformedURLException {
        /*
        Injector injector = createInjector((Module[]) new Module[]{new JpaPersistModule("rpfl")});
        ResourceFingerprintDao fingerprintDao = (ResourceFingerprintDao)injector.getInstance((Class)ResourceFingerprintDao.class);
        ((PersistService)injector.getInstance((Class)PersistService.class)).start();
        URL url1 = new URL("http://www.google.de");
        ResourceFingerprint resourceFingerprint1 = new ResourceFingerprint(url1, new byte[64], Messages.VerificationStrength.downloaded, new byte[64]);
        URL url2 = new URL("http://www.google.de");
        ResourceFingerprint resourceFingerprint2 = new ResourceFingerprint(url2, new byte[64], Messages.VerificationStrength.downloaded, new byte[64]);
        fingerprintDao.save(ImmutableSet.of(resourceFingerprint1, resourceFingerprint2));
        */
    }
}
