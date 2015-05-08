package org.rpfl.db.domain;

import com.google.common.base.Objects;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.URL;

import static com.google.common.base.Objects.equal;
import static org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL;
import static org.rpfl.transport.protobuf.Messages.VerificationStrength;

@Entity
@Table(name="resource_fingerprints")
@Cacheable
@Cache(usage = TRANSACTIONAL)
public class ResourceFingerprint {

    @Id
    @NotNull
    private URL url;

    @Column
    @NotNull
    @Size(min=64, max=64)
    private byte[] hash;

    @Column(name="verification_strength")
    @NotNull
    @Enumerated(value=EnumType.ORDINAL)
    private VerificationStrength verificationStrength;

    public ResourceFingerprint(){
    }

    public ResourceFingerprint(URL url, byte[] hash, VerificationStrength verificationStrength) {
        this.url = url;
        this.hash = hash;
        this.verificationStrength = verificationStrength;
    }


    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public VerificationStrength getVerificationStrength() {
        return verificationStrength;
    }

    public void setVerificationStrength(VerificationStrength verificationStrength) {
        this.verificationStrength = verificationStrength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceFingerprint that = (ResourceFingerprint) o;

        return equal(this.url, that.url) &&
                equal(this.hash, that.hash) &&
                equal(this.verificationStrength, that.verificationStrength);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url, hash, verificationStrength);
    }
}
