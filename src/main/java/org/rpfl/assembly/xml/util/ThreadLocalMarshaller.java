package org.rpfl.assembly.xml.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

@Singleton
public class ThreadLocalMarshaller extends ThreadLocal<Marshaller> {

    @Inject
    private JAXBContext jaxbContext;

    @Override
    protected Marshaller initialValue() {
        try {
            return this.jaxbContext.createMarshaller();
        }
        catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
