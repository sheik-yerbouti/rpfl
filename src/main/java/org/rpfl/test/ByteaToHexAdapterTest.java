package org.rpfl.test;

import org.junit.Test;
import org.rpfl.assembly.xml.adapters.ByteaToHexAdapter;
import java.util.Random;
import static org.junit.Assert.assertArrayEquals;

public class ByteaToHexAdapterTest {
    private ByteaToHexAdapter subject = new ByteaToHexAdapter();

    @Test
    public void test_positive() throws Exception {
        Random random = new Random();

        for(int size = 0; size < 5000; size++){
            byte[] bytes = new byte[size];

            random.nextBytes(bytes);

            String marshalled = subject.marshal(bytes);

            byte[] unmarshalled = subject.unmarshal(marshalled);

            assertArrayEquals(bytes, unmarshalled);

            String marshalledUpper = marshalled.toUpperCase();

            byte[] unmarshalledFromUpper = subject.unmarshal(marshalledUpper);

            assertArrayEquals(unmarshalled, unmarshalledFromUpper);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_negative() throws Exception {
        String badInput = "Hallo Welt";

        subject.unmarshal(badInput);
    }
}
