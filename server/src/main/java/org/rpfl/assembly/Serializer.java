package org.rpfl.assembly;

import com.google.inject.Singleton;
import com.google.protobuf.ByteString;
import org.iq80.snappy.SnappyFramedInputStream;
import org.rpfl.db.domain.ResourceFingerprint;
import org.rpfl.transport.protobuf.Messages;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.iq80.snappy.Snappy.compress;
import static org.rpfl.transport.protobuf.Messages.Request.parseFrom;
import static org.rpfl.transport.protobuf.Messages.Response;
import static org.rpfl.transport.protobuf.Messages.ResponseEntry;

@Singleton
public class Serializer {
    public Messages.Request deserialize(InputStream inputStream) throws IOException {
        return parseFrom(new SnappyFramedInputStream(inputStream, true));
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
