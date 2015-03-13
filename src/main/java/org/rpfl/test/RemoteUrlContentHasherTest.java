package org.rpfl.test;

import org.junit.Test;

import java.io.IOException;
import java.security.SignatureException;

public class RemoteUrlContentHasherTest {
    @Test
    public void test() throws IOException, SignatureException {
        /*
        Injector injector = createInjector(new CryptoModule("tmp"));
        InputStreamHasher instance = injector.getInstance(InputStreamHasher.class);
        byte[] bytes = instance.getVerificationData(this.getClass().getResource("/lorem-ipsum.txt").openStream());
        assertThat(bytes, not(nullValue()));
        assertThat(bytes.length, is(64));
        byte[] bytesReloaded = instance.getVerificationData(this.getClass().getResource("/lorem-ipsum.txt").openStream());
        assertThat(bytesReloaded, not(nullValue()));
        assertThat(bytesReloaded.length, is(64));
        assertThat(bytesReloaded, equalTo(bytes));
        byte[] bytes2 = instance.getVerificationData(this.getClass().getResource("/lorem-ipsum_2.txt").openStream());
        assertEquals(bytes.length, bytes2.length);
        assertFalse(areEqual(bytes, bytes2));
        */
    }
}
