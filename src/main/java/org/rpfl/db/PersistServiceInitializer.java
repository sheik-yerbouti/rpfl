package org.rpfl.db;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;

import static java.lang.System.err;

public class PersistServiceInitializer {
    @Inject
    PersistServiceInitializer(PersistService service) {
        service.start();
        err.println("PersistService started");
    }
}
