package com.acme.myapp;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import javax.inject.Singleton;

import java.nio.file.Path;
import java.nio.file.Paths;

/** The main entry point of this application, configured in web.xml. */
public class Main extends GuiceServletContextListener {
  @Override
  protected Injector getInjector() {
    return Guice.createInjector(new ServerModule());
  }
}

/** Guice bindings for this application. */
class ServerModule extends ServletModule {
  @Override
  public void configureServlets() {
    // Bind converters for JAXB/JSON serialization.
    bind(JacksonJsonProvider.class).in(Singleton.class);

    bind(Path.class).toInstance(Paths.get("/usr/share/dict/words"));

    // Since the line below doesn't work, we must manually bind our resources.
    bind(EmailResource.class);

    // This doesn't work for me and probably has something to do with Java 1.8.
    // serve("/rest/*").with(GuiceContainer.class, ImmutableMap.of(
    //    PackagesResourceConfig.PROPERTY_PACKAGES, "com.acme.myapp"));

    // The JSON REST API is available starting at /rest/*.
    serve("/rest/*").with(GuiceContainer.class);
  }
}

