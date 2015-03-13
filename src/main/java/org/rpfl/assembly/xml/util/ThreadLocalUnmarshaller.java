package org.rpfl.assembly.xml.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

@Singleton
public class ThreadLocalUnmarshaller extends ThreadLocal<Unmarshaller> {

    @Inject
    private JAXBContext jaxbContext;

    @Override
    protected Unmarshaller initialValue() {
        try {
            return this.jaxbContext.createUnmarshaller();
        }
        catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
