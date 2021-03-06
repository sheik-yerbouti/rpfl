package org.rpfl.assembly;

import com.google.inject.Singleton;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.iq80.snappy.CorruptionException;
import org.iq80.snappy.Snappy;
import org.iq80.snappy.SnappyFramedInputStream;
import org.rpfl.db.domain.ResourceFingerprint;
import org.rpfl.transport.protobuf.Messages;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.iq80.snappy.Snappy.compress;
import static org.iq80.snappy.Snappy.uncompress;
import static org.rpfl.transport.protobuf.Messages.Request.parseFrom;
import static org.rpfl.transport.protobuf.Messages.Response;
import static org.rpfl.transport.protobuf.Messages.ResponseEntry;

@Singleton
public class Serializer {
    public Messages.Request deserialize(byte[] input) throws CorruptionException, InvalidProtocolBufferException
    {
        return parseFrom(uncompress(input, 0, input.length));
    }

    public byte[] serialize(Set<ResourceFingerprint> resourceFingerprints) throws IOException {
        checkNotNull(resourceFingerprints);

        Response.Builder builder = Response.newBuilder();

        for (ResourceFingerprint resourceFingerprint : resourceFingerprints) {
            ResponseEntry.Builder entryBuilder = builder.addEntriesBuilder();
            entryBuilder.setUrl(resourceFingerprint.getUrl().toString());
            entryBuilder.setHash(ByteString.copyFrom(resourceFingerprint.getHash()));
            entryBuilder.setVerificationStrength(resourceFingerprint.getVerificationStrength());
            entryBuilder.build();
        }

        return compress(
            builder
                .build()
                .toByteArray()
        );
    }
}
