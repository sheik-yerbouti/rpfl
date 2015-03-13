package org.rpfl.assembly.xml;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.rpfl.assembly.common.FingerprintProvider;
import org.rpfl.assembly.common.RequestHandler;
import org.rpfl.assembly.xml.messages.RequestMessage;
import org.rpfl.assembly.xml.messages.ResponseMessage;
import org.rpfl.assembly.xml.messages.ResponseMessageEntry;
import org.rpfl.assembly.xml.util.ThreadLocalMarshaller;
import org.rpfl.assembly.xml.util.ThreadLocalUnmarshaller;
import org.rpfl.db.domain.ResourceFingerprint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.security.SignatureException;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static javax.servlet.http.HttpServletResponse.*;

@Singleton
public class XmlRequestHandler implements RequestHandler {

    @Inject
    private ThreadLocalMarshaller threadLocalMarshaller;

    @Inject
    private ThreadLocalUnmarshaller threadLocalUnmarshaller;

    @Inject
    private FingerprintProvider fingerprintProvider;

    @Inject
    private XMLInputFactory xmlInputFactory;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, SignatureException {
        try {
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(request.getInputStream());

            RequestMessage requestMessage = threadLocalUnmarshaller
                                                .get()
                                                .unmarshal(xmlStreamReader, RequestMessage.class)
                                                .getValue();

            Set<ResourceFingerprint> resourceFingerprints = fingerprintProvider.get(requestMessage.getUrls());

            Set<ResponseMessageEntry> responseMessageEntries = resourceFingerprints
                                                                    .stream()
                                                                    .map(ResponseMessageEntry::new)
                                                                    .collect(toSet());

            ResponseMessage responseMessage = new ResponseMessage(responseMessageEntries);

            threadLocalMarshaller
                    .get()
                    .marshal(responseMessage, response.getOutputStream());
        }
        catch (JAXBException | XMLStreamException e) {
            response.getWriter().print(e.getMessage());
            response.setStatus(SC_BAD_REQUEST);
        }
    }
}
