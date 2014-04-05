package com.acme.notepad.restapi;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.notepad.model.Note;
import com.acme.notepad.model.NoteMetadata;
import com.acme.notepad.persistence.NoteDao;
import com.acme.notepad.persistence.Selector;

@Singleton
@Path("/")
public class NoteResource {
  private static final Logger LOG = LoggerFactory.getLogger(NoteResource.class);
  private final NoteDao dao;

  @Inject
  NoteResource(NoteDao dao) {
    this.dao = dao;
  }

  // TODO: Validation!
  // TODO: Sort, search and pagination.

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<NoteMetadata> metadatas() {
    LOG.info("GET / invoked" );
    return dao.search(Selector.builder()
        .withType(NoteMetadata.class)
        .withOrdering("lastModified asc")
        .build());
  }

  @GET
  @Path("/{metadataId}")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public Note noteFor(@PathParam("metadataId") long metadataId) {
    LOG.info("GET /{} invoked", metadataId);
    return dao.lookupByNoteMetadaId(metadataId);
  }

  @PUT
  public void save(Note note) {
    LOG.info("PUT / invoked for {}", note);
    dao.save(note);
  }

  @DELETE
  public void delete(Note note) {
    LOG.info("DELETE / invoked for {}", note);
    dao.delete(note);
  }
}