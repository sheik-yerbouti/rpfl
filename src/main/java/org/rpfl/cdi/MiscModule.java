package org.rpfl.cdi;

import com.google.inject.AbstractModule;
import org.apache.logging.log4j.Logger;
import org.rpfl.assembly.xml.messages.RequestMessage;
import org.rpfl.assembly.xml.messages.ResponseMessage;
import org.rpfl.db.PersistServiceInitializer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLInputFactory;
import java.util.concurrent.ExecutorService;

import static java.lang.Runtime.getRuntime;
import static java.util.concurrent.Executors.newWorkStealingPool;
import static javax.xml.bind.JAXBContext.newInstance;
import static org.apache.logging.log4j.LogManager.getLogger;

public class MiscModule extends AbstractModule {

    private static final int THREADS_PER_PROCESSOR = 4;

    protected void configure() {

        bind(ExecutorService.class).toInstance(newWorkStealingPool(getRuntime().availableProcessors() * THREADS_PER_PROCESSOR));

        bind(PersistServiceInitializer.class).asEagerSingleton();

        bind(Logger.class).toInstance(getLogger());

        try {
            bind(JAXBContext.class).toInstance(newInstance(RequestMessage.class, ResponseMessage.class));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        bind(XMLInputFactory.class).toInstance(XMLInputFactory.newFactory());
    }
}
