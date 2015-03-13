package org.rpfl.cdi;

import org.rpfl.endpoint.RpflServlet;

public class ServletModule extends com.google.inject.servlet.ServletModule {
    protected void configureServlets()
    {
        serve("/").with(RpflServlet.class);
    }
}
