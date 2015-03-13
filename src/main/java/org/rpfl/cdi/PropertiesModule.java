package org.rpfl.cdi;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import org.rpfl.assembly.common.RequestHandler;
import org.rpfl.assembly.protosnap.ProtoSnapRequestHandler;
import org.rpfl.assembly.xml.XmlRequestHandler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Splitter.on;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.inject.name.Names.named;
import static java.lang.System.err;
import static java.util.Optional.empty;

public class PropertiesModule extends AbstractModule{
    @Override
    protected void configure() {
        Properties properties = new Properties();

        try {
            properties.load(getClass().getResourceAsStream("/rpfl.properties"));
        } catch (IOException e) {
            throw new RuntimeException("properties could not be loaded", e);
        }

        bindRepositoryUrl(properties);
        bindAllowedFileExtensions(properties);
        bindLocalBuildDirectory(properties);
        bindTransport(properties);
    }

    private void bindTransport(Properties properties) {
        String transport = Optional.ofNullable(properties.getProperty("transport")).orElse("");

        switch (transport){
            case "":
            case "protosnap":
                bind(RequestHandler.class).to(ProtoSnapRequestHandler.class);
                err.println("transport is protosnap");
                break;
            case "xml":
                bind(RequestHandler.class).to(XmlRequestHandler.class);
                err.println("transport is xml");
                break;
            default:
                throw new IllegalArgumentException("'" + transport + "' is not a valid transport");
        }
    }

    private void bindRepositoryUrl(Properties properties) {
        try {
            URL repositoryUrl = new URL(properties.getProperty("repositoryUrl"));
            bind(URL.class).annotatedWith(named("repositoryUrl")).toInstance(repositoryUrl);
            err.println("repository url is " + repositoryUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("property 'repositoryUrl' is not present or not a valid url");
        }
    }

    private void bindAllowedFileExtensions(Properties properties) {
        String allowedFileExtensionsUnsplit = properties.getProperty("allowedFileExtensions");

        Set<String> allowedFileExtensions = copyOf(
                on(",")
                        .trimResults()
                        .split(allowedFileExtensionsUnsplit)
        );

        err.println("allowed file extensions are " + allowedFileExtensionsUnsplit);

        bind(new TypeLiteral<Set<String>>(){}).annotatedWith(named("allowedFileExtensions")).toInstance(allowedFileExtensions);
    }

    private void bindLocalBuildDirectory(Properties properties) {
        String localBuildDirectoryProperty = properties.getProperty("localBuildDirectory");

        Optional<Path> localBuildDirectoryOptional;

        if(isNullOrEmpty(localBuildDirectoryProperty)){
            localBuildDirectoryOptional = empty();
        } else {
            Path path = Paths.get(localBuildDirectoryProperty);

            checkArgument(path.isAbsolute());

            File file = path.toFile();

            checkArgument(file.exists());
            checkArgument(file.isDirectory());

            localBuildDirectoryOptional = Optional.of(path);

            err.println("local build directory is " + path);
        }

        bind(new TypeLiteral<Optional<Path>>(){}).annotatedWith(named("localBuildDirectory")).toInstance(localBuildDirectoryOptional);
    }
}
