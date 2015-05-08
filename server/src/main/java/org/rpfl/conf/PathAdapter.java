package org.rpfl.conf;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.base.Strings.isNullOrEmpty;

public class PathAdapter extends XmlAdapter<String, Path> {

    @Override
    public Path unmarshal(String s) throws Exception {
        return isNullOrEmpty(s) ? null : Paths.get(s);
    }

    @Override
    public String marshal(Path path) throws Exception {
        return path != null ? path.toAbsolutePath().toString() : "";
    }
}
