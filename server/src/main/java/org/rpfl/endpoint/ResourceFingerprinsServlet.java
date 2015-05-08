package org.rpfl.endpoint;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.rpfl.assembly.FingerprintProvider;
import org.rpfl.assembly.Serializer;
import org.rpfl.crypt.EddsaSigner;
import org.rpfl.db.domain.ResourceFingerprint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static org.rpfl.transport.protobuf.Messages.Request;

@Singleton
public class ResourceFingerprinsServlet extends HttpServlet {

    @Inject
    private FingerprintProvider fingerprintProvider;

    @Inject
    private Serializer serializer;

    @Inject
    private EddsaSigner eddsaSigner;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Request requestMessage = serializer.deserialize(request.getInputStream());

        checkNotNull(requestMessage);
        checkArgument(!requestMessage.getResourcesList().isEmpty());

        Set<URL> resources = newHashSetWithExpectedSize(requestMessage.getResourcesList().size());

        for (String resource : requestMessage.getResourcesList()) {
            resources.add(new URL(resource));
        }

        checkArgument(requestMessage.getResourcesCount() == resources.size(), "requested resources must not contain duplicates");

        Set<ResourceFingerprint> resourceFingerprints = fingerprintProvider.get(resources);

        byte[] payload = serializer.serialize(resourceFingerprints);

        try {
            response.getOutputStream().write(eddsaSigner.sign(payload));
            response.getOutputStream().write(payload);
        } catch (Throwable e) {
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(SC_METHOD_NOT_ALLOWED);
    }
}
