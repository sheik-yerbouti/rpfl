package org.rpfl.assembly.xml.messages;

import org.rpfl.assembly.xml.adapters.ByteaToHexAdapter;
import org.rpfl.assembly.xml.adapters.UrlAdapter;
import org.rpfl.db.domain.ResourceFingerprint;
import org.rpfl.transport.protobuf.Messages;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URL;

public class ResponseMessageEntry {
    private final ResourceFingerprint resourceFingerprint;

    public ResponseMessageEntry(ResourceFingerprint resourceFingerprint) {
        this.resourceFingerprint = resourceFingerprint;
    }

    @XmlJavaTypeAdapter(value=UrlAdapter.class)
    @XmlAttribute
    public URL getUrl() {
        return this.resourceFingerprint.getUrl();
    }

    @XmlJavaTypeAdapter(value=ByteaToHexAdapter.class)
    @XmlAttribute
    public byte[] getHash() {
        return this.resourceFingerprint.getHash();
    }

    @XmlAttribute
    public long getContentSize(){
        return this.resourceFingerprint.getSize();
    }

    @XmlAttribute
    public Messages.VerificationStrength getVerificationStrength() {
        return this.resourceFingerprint.getVerificationStrength();
    }
}
