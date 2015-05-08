package org.rpfl.cdi;

import com.google.inject.AbstractModule;
import org.rpfl.conf.RpflConfiguration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class PropertiesModule extends AbstractModule {
    @Override
    protected void configure() {
        try {
            RpflConfiguration rpflConfiguration = (RpflConfiguration)
                    JAXBContext
                    .newInstance(RpflConfiguration.class)
                    .createUnmarshaller()
                    .unmarshal(getClass().getResourceAsStream("rpfl.xml"));

            bind(RpflConfiguration.class).toInstance(rpflConfiguration);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
