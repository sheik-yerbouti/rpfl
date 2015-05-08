package org.rpfl.cdi;

import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import org.rpfl.crypt.CryptoModule;

import static com.google.inject.Guice.createInjector;

public class ContextListener extends GuiceServletContextListener {
    protected Injector getInjector() {
        return createInjector(
                new ServletModule(),
                new JpaPersistModule("rpfl"),
                new MiscModule(),
                new CryptoModule("private_key"),
                new PropertiesModule()
        );
    }
}
