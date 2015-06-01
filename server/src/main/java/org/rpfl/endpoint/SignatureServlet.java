package org.rpfl.endpoint;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.rpfl.assembly.FingerprintProvider;
import org.rpfl.assembly.Serializer;
import org.rpfl.crypt.EddsaSigner;
import org.rpfl.crypt.ThreadLocalBuffer;
import org.rpfl.crypt.ThreadLocalByteArrayOutputStream;
import org.rpfl.crypt.ThreadLocalMessageDigest;
import org.rpfl.db.domain.ResourceFingerprint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.*;
import static com.google.common.collect.Sets.*;
import static java.util.stream.Collectors.*;
import static javax.servlet.http.HttpServletResponse.*;
import static org.rpfl.transport.protobuf.Messages.*;

@Singleton
public class SignatureServlet extends HttpServlet {

    @Inject
    private FingerprintProvider fingerprintProvider;

    @Inject
    private Serializer serializer;

    @Inject
    private EddsaSigner eddsaSigner;

    @Inject
    private ThreadLocalMessageDigest threadLocalMessageDigest;

    @Inject
    private ThreadLocalBuffer threadLocalBuffer;

    @Inject
    private ThreadLocalByteArrayOutputStream threadLocalByteArrayOutputStream;

    @Inject
    @Named("by_url")
    private Comparator<ResourceFingerprint> urlComparator;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        byte[] input = getInputBytes(request);

        Request requestMessage = serializer.deserialize(input);

        checkNotNull(requestMessage);

        checkArgument(!requestMessage.getResourcesList().isEmpty());

        Set<URL> resources = getUrlsSorted(requestMessage);

        MessageDigest messageDigest = threadLocalMessageDigest.get();

        messageDigest.update(input);

        fingerprintProvider
                .get(resources)
                .stream()
                .sorted(urlComparator)
                .forEach(rfp -> messageDigest.update(rfp.getHash()));

        byte[] hash = messageDigest.digest();

        byte[] signature = eddsaSigner.sign(hash);

        checkNotNull(signature);
        checkState(signature.length == 64);

        response.getOutputStream().write(signature);
    }

    private Set<URL> getUrlsSorted(Request requestMessage) throws MalformedURLException
    {
        Set<URL> resources = newHashSetWithExpectedSize(requestMessage.getResourcesList().size());

        List<String> resourcesSorted = requestMessage
                                            .getResourcesList()
                                            .stream()
                                            .sorted()
                                            .collect(toList());

        for (String resource : resourcesSorted) {
            resources.add(new URL(resource));
        }

        checkArgument(requestMessage.getResourcesCount() == resources.size(), "requested resources must not contain duplicates");
        return resources;
    }

    private byte[] getInputBytes(HttpServletRequest request) throws IOException
    {
        int read;

        byte[] buffer = threadLocalBuffer.get();

        ByteArrayOutputStream byteArrayOutputStream = threadLocalByteArrayOutputStream.get();

        do{
            read = request.getInputStream().read(buffer);
            byteArrayOutputStream.write(buffer, 0, read);
        } while (read == buffer.length);

        return byteArrayOutputStream.toByteArray();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(SC_METHOD_NOT_ALLOWED);
    }
}
