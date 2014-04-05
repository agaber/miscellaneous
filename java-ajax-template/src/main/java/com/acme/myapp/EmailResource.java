package com.acme.myapp;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllLines;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Path("/email")
@Singleton
public class EmailResource {
  private static final Logger LOG = LoggerFactory.getLogger(EmailResource.class);
  private final ImmutableSet<String> dictionary;

  @Inject
  EmailResource(java.nio.file.Path path) throws IOException {
    LOG.info("Using dictionary file {}.", path.toAbsolutePath());
    this.dictionary = ImmutableSet.copyOf(readAllLines(path, UTF_8)
        .parallelStream()
        .map(String::toLowerCase)
        .collect(toSet()));
  }

  @POST
  public void send(Email email) {
    LOG.info(email.toString());
    // http://www.tutorialspoint.com/java/java_sending_email.htm

  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<String> list() {
    return dictionary.stream().sorted().collect(toList());
  }

  @GET
  @Path("/spellcheck")
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Boolean> spellCheck(@QueryParam("words") List<String> words) {
    return words.stream().collect(toMap(
        identity(),
        s -> dictionary.contains(s.toLowerCase())));
  }
}
