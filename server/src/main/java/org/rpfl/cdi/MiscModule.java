package org.rpfl.cdi;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import org.apache.logging.log4j.Logger;
import org.rpfl.db.PersistServiceInitializer;
import org.rpfl.db.domain.ResourceFingerprint;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;

import static com.google.inject.name.Names.named;
import static java.lang.Runtime.getRuntime;
import static java.util.concurrent.Executors.newWorkStealingPool;
import static org.apache.logging.log4j.LogManager.getLogger;

public class MiscModule extends AbstractModule {

    private static final int THREADS_PER_PROCESSOR = 4;

    protected void configure() {

        bind(ExecutorService.class).toInstance(newWorkStealingPool(getRuntime().availableProcessors() * THREADS_PER_PROCESSOR));

        bind(PersistServiceInitializer.class).asEagerSingleton();

        bind(Logger.class).toInstance(getLogger());

        bind(new TypeLiteral<Comparator<ResourceFingerprint>>(){})
                .annotatedWith(named("by_url"))
                .toInstance((rfp1, rfp2) -> rfp1.getUrl().toString().compareTo(rfp2.getUrl().toString()));
    }
}
