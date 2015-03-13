package org.rpfl.assembly.protosnap;

import com.google.inject.Singleton;
import com.google.protobuf.ByteString;
import org.rpfl.db.domain.ResourceFingerprint;

import java.io.IOException;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.iq80.snappy.Snappy.compress;
import static org.rpfl.transport.protobuf.Messages.Response;
import static org.rpfl.transport.protobuf.Messages.ResponseEntry;

@Singleton
public class ProtoSnapSerializer {
    public byte[] serialize(Set<ResourceFingerprint> resourceFingerprints) throws IOException {
        checkNotNull(resourceFingerprints);

        Response.Builder builder = Response.newBuilder();

        for (ResourceFingerprint resourceFingerprint : resourceFingerprints) {
            ResponseEntry.Builder entryBuilder = builder.addEntriesBuilder();
            entryBuilder.setUrl(resourceFingerprint.getUrl().toString());
            entryBuilder.setSize(resourceFingerprint.getSize());
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
