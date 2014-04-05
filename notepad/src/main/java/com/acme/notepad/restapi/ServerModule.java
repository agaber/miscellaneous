package com.acme.notepad.restapi;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class ServerModule extends ServletModule {

  @Override
  public void configureServlets() {
    // Bind converters for JAXB/JSON serialization.
    bind(MessageBodyReader.class).to(JacksonJsonProvider.class);
    bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);

    // Bind the reset resources.
    // TODO: Find out why this explicit binding is needed.
    bind(NoteResource.class);

    // Persistence.
    bind(PersistenceManagerFactory.class)
        .toInstance(JDOHelper.getPersistenceManagerFactory("mongodb"));

    // The JSON REST API is available starting at /rest/*.
    serve("/rest/*").with(GuiceContainer.class);
  }
}
