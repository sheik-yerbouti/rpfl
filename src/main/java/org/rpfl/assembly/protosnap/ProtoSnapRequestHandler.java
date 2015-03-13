package org.rpfl.assembly.protosnap;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.iq80.snappy.SnappyFramedInputStream;
import org.rpfl.assembly.common.FingerprintProvider;
import org.rpfl.assembly.common.RequestHandler;
import org.rpfl.crypt.EddsaSigner;
import org.rpfl.db.domain.ResourceFingerprint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.SignatureException;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;
import static org.rpfl.transport.protobuf.Messages.Request;
import static org.rpfl.transport.protobuf.Messages.Request.parseFrom;

@Singleton
public class ProtoSnapRequestHandler implements RequestHandler {

    @Inject
    private FingerprintProvider fingerprintProvider;

    @Inject
    private ProtoSnapSerializer protoSnapSerializer;

    @Inject
    private EddsaSigner eddsaSigner;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, SignatureException {

        InputStream decompressedInputStream = new SnappyFramedInputStream(request.getInputStream(), true);

        Request requestMessage = parseFrom(decompressedInputStream);

        checkNotNull(requestMessage);

        checkArgument(requestMessage.getResourcesCount() > 0);

        Set<URL> resources = newHashSetWithExpectedSize(requestMessage.getResourcesCount());

        for (String resource : requestMessage.getResourcesList()) {
            resources.add(new URL(resource));
        }

        checkArgument(requestMessage.getResourcesCount() == resources.size(), "requested resources must not contain duplicates");

        Set<ResourceFingerprint> resourceFingerprints = fingerprintProvider.get(resources);

        byte[] protosnap = protoSnapSerializer.serialize(resourceFingerprints);
        byte[] signature = eddsaSigner.sign(protosnap);

        OutputStream outputStream = response.getOutputStream();

        outputStream.write(signature);
        outputStream.write(protosnap);
    }
}
