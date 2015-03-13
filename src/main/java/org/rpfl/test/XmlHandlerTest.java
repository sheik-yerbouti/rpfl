package org.rpfl.test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.junit.Test;
import org.rpfl.assembly.xml.messages.RequestMessage;
import org.rpfl.assembly.xml.util.ThreadLocalMarshaller;
import org.rpfl.cdi.MiscModule;

import javax.xml.bind.JAXBException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import static com.google.inject.Guice.createInjector;
import static java.lang.System.out;

public class XmlHandlerTest {

    @Test
    public void testXmlHandler() throws MalformedURLException, JAXBException {
        Injector injector = createInjector(new MiscModule(), new JpaPersistModule("rpfl"));

        ThreadLocalMarshaller marshaller = injector.getInstance(ThreadLocalMarshaller.class);

        URL url1 = new URL("http://www.google.de");

        URL url2 = new URL("http://www.google.com");

        Set<URL> urls = ImmutableSet.of(url1, url2);

        RequestMessage r = new RequestMessage(urls);

        marshaller.get().marshal(r, out);
    }
}
