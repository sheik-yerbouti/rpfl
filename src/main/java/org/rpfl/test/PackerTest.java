package org.rpfl.test;

public class PackerTest {
    /*
    @Test
    public void testPacker() throws IOException {
        Packer packer = (Packer)Guice.createInjector((Module[])new Module[0]).getInstance((Class)Packer.class);
        HashSet<ResourceFingerprint> resourceFingerprints = Sets.newHashSet();

        for (int i = 0; i < 1000; ++i) {
            resourceFingerprints.add(this.createResourceFingerprint());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        packer.pack(resourceFingerprints, outputStream);

        ImmutableList resourceFingerprintList = ImmutableList.copyOf((Collection)resourceFingerprints);

        byte[] packed = outputStream.toByteArray();

        assertThat(packed, is(notNullValue()));

        Messages.Response message = parseFrom(new SnappyFramedInputStream(new ByteArrayInputStream(packed), true));

        assertThat(message.getEntriesCount(), is(1000));

        for (int i2 = 0; i2 < 1000; ++i2) {
            Messages.ResponseEntry entry = message.getEntries(i2);
            ResourceFingerprint resourceFingerprint = (ResourceFingerprint)resourceFingerprintList.get(i2);
            assertThat(entry.getUrl(), equalTo(resourceFingerprint.getUrl().toString()));
            assertThat(entry.getVerificationStrength(), equalTo(resourceFingerprint.getVerificationStrength()));
            byte[] actualHash = entry.getSignature().toByteArray();
            byte[] expectedHash = resourceFingerprint.getSignature();
            assertArrayEquals(expectedHash, actualHash);
        }
    }

    private ResourceFingerprint createResourceFingerprint() throws MalformedURLException {
        Random random = new Random();
        ResourceFingerprint resourceFingerprint = new ResourceFingerprint(url, size, hash, signature, verificationStrength);
        resourceFingerprint.setVerificationStrength(Messages.VerificationStrength.valueOf(1 + random.nextInt(3)));
        byte[] hash = new byte[64];
        random.nextBytes(hash);
        resourceFingerprint.setSignature(hash);
        switch (random.nextInt(2)) {
            case 0: {
                resourceFingerprint.setUrl(new URL("http://www.google.com/" + random.nextInt()));
                break;
            }
            case 1: {
                resourceFingerprint.setUrl(new URL("http://www.yahoo.com/" + random.nextInt()));
                break;
            }
            case 2: {
                resourceFingerprint.setUrl(new URL("http://www.amazon.com/" + random.nextInt()));
            }
        }
        return resourceFingerprint;
    }
    */
}
