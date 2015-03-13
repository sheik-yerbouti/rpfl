package org.rpfl.assembly.xml.messages;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

@XmlRootElement(name="verification_response")
public class ResponseMessage {
    @XmlElementWrapper(name="verified_resources", required=true)
    @XmlElement(name="resource")
    private Set<ResponseMessageEntry> resourceFingerprints;

    public ResponseMessage(){

    }

    public ResponseMessage(Set<ResponseMessageEntry> resourceFingerprints) {
        this.resourceFingerprints = resourceFingerprints;
    }

    public Set<ResponseMessageEntry> getResourceFingerprints() {
        return this.resourceFingerprints;
    }
}
