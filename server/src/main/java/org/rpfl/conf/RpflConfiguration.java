package org.rpfl.conf;

import com.google.common.collect.ImmutableSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

@XmlRootElement
public class RpflConfiguration {

    @XmlElement(name = "repository")
    private Set<Repository> repositories = ImmutableSet.of(new Repository());

    public Set<Repository> getRepositories() {
        return repositories;
    }
}
