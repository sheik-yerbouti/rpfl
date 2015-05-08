package org.rpfl.cdi;

import org.rpfl.endpoint.ResourceFingerprinsServlet;
import org.rpfl.endpoint.SignatureServlet;

public class ServletModule extends com.google.inject.servlet.ServletModule {
    protected void configureServlets(){
        serve("/signatures").with(SignatureServlet.class);
        serve("/fingerprints").with(ResourceFingerprinsServlet.class);
    }
}
