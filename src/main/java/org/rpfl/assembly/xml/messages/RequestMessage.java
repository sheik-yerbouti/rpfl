package org.rpfl.assembly.xml.messages;

import org.rpfl.assembly.xml.adapters.UrlAdapter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URL;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

@XmlRootElement(name="verification_request")
public class RequestMessage {

    @SuppressWarnings("unused")
    public RequestMessage(){
    }

    public RequestMessage(Set<URL> urls) {
        this.urls = checkNotNull(urls);
    }

    @XmlElementWrapper(name="requested_resources", required=true)
    @XmlElement(name="url")
    @XmlJavaTypeAdapter(value=UrlAdapter.class)
    @NotNull
    @Size(min=1)
    private Set<URL> urls;

    public Set<URL> getUrls() {
        return this.urls;
    }
}
