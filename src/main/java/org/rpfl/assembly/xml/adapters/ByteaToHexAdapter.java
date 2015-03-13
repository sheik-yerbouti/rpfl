package org.rpfl.assembly.xml.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Character.toLowerCase;
import static java.util.Arrays.binarySearch;

public class ByteaToHexAdapter extends XmlAdapter<String, byte[]> {
    private final char[] chars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    @Override
    public byte[] unmarshal(String s) throws Exception {
        checkNotNull(s);
        checkArgument((s.length() % 2 == 0), "hexadecimal representation of byte array must have even length");

        byte[] unmarshalled = new byte[s.length() / 2];

        for (int i = 0; i < s.length(); i+=2) {
            char upper = toLowerCase(s.charAt(i));
            char lower = toLowerCase(s.charAt(i + 1));
            int upperByte = binarySearch(chars, upper);
            int lowerByte = binarySearch(chars, lower);

            checkArgument(upperByte >= 0 && upperByte < chars.length, "character %s is not allowed in hexadecimal string", upper);
            checkArgument(lowerByte >= 0 && lowerByte < chars.length, "character %s is not allowed in hexadecimal string", lower);

            int arrayIndex = i / 2;

            unmarshalled[arrayIndex] = (byte)((upperByte << 4) | lowerByte);
        }

        return unmarshalled;
    }

    @Override
    public String marshal(byte[] bytes) throws Exception {
        checkNotNull(bytes);
        StringBuilder stringBuilder = new StringBuilder(bytes.length * 2);

        for (byte aByte : bytes) {
            stringBuilder.append(chars[(aByte >> 4) & 0x0f]);
            stringBuilder.append(chars[aByte & 0x0f]);
        }

        return stringBuilder.toString();
    }
}
