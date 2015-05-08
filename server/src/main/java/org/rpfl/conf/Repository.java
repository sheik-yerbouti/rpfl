package org.rpfl.conf;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URL;
import java.nio.file.Path;
import java.util.Set;

public class Repository {

    @XmlAttribute
    private URL url;

    @XmlAttribute
    @XmlJavaTypeAdapter(PathAdapter.class)
    private Path localPath;

    @XmlElementWrapper(name = "allowedExtensions")
    @XmlElement(name = "extension")
    private Set<String> allowedFileExtensions;

    public URL getUrl() {
        return url;
    }

    public Set<String> getAllowedFileExtensions() {
        return allowedFileExtensions;
    }

    public Path getLocalPath() {
        return localPath;
    }
}
