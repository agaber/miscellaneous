package com.acme.notepad.restapi;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.acme.notepad.model.Note;
import com.acme.notepad.model.NoteMetadata;
import com.acme.notepad.persistence.NoteDao;
import com.acme.notepad.persistence.Selector;
import com.google.common.collect.Lists;

public class NoteResourceTest {
  @Mock NoteDao dao;

  // Under test.
  NoteResource resource;

  @Before
  public void beforeEach() throws Exception {
    MockitoAnnotations.initMocks(this);
    resource = new NoteResource(dao);
  }

  @Test
  public void listNoteMetadatas() throws Exception {
    List<NoteMetadata> expecteds = Lists.newArrayList(
        newMetadata(1),
        newMetadata(2),
        newMetadata(3));
    when(dao.<NoteMetadata>search(Selector.builder()
        .withType(NoteMetadata.class)
        .withOrdering("lastModified asc")
        .build()))
        .thenReturn(expecteds);
    assertContentsInOrder(
        "Should get metadatas.",
        resource.metadatas(),
        newMetadata(1),
        newMetadata(2),
        newMetadata(3));
  }

  @Test
  public void noteForNoteMetadataId() throws Exception {
    Note note = newNote(1, 2);
    when(dao.lookupByNoteMetadaId(note.metadata.id)).thenReturn(note);
    assertEquals(note, resource.noteFor(note.metadata.id));
  }

  @Test
  public void save() throws Exception {
    Note note = newNote(100, 2);
    resource.save(note);
    verify(dao, atMost(1)).save(note);
  }

  public void delete() throws Exception {
    Note note = newNote(300, 4);
    resource.delete(note);
    verify(dao, atMost(1)).delete(note);
  }

  private void assertContentsInOrder(String message, List<?> actuals, Object... expecteds) {
    assertEquals(message, expecteds.length, actuals.size());
    assertEquals(
        message,
        Lists.newArrayList(expecteds).toString(),
        Lists.newArrayList(actuals).toString());
  }

  private Note newNote(long id, long metadataId) {
    Note note = new Note();
    note.id = id;
    note.metadata = newMetadata(metadataId);
    return note;
  }

  private NoteMetadata newMetadata(long id) {
    NoteMetadata m1 = new NoteMetadata();
    m1.id = id;
    m1.title = "m" + id;
    m1.lastModified = new DateTime(2014, 01, new Long(id).intValue(), 0, 0).toDate();
    return m1;
  }
}
