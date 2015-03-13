package org.rpfl.assembly.xml.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.net.URL;

public class UrlAdapter
extends XmlAdapter<String, URL> {
    @Override
    public URL unmarshal(String s) throws Exception {
        return new URL(s);
    }

    @Override
    public String marshal(URL url) throws Exception {
        return url.toString();
    }
}
