package com.acme.notepad.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.acme.notepad.model.Note;
import com.acme.notepad.model.NoteMetadata;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * Functional database tests.
 *
 * <p>A lot of this is experimental, but these tests (and the test framework
 * itself) are poorly designed and probably flaky.
 */
@RunWith(MongoDatabaseTestRunner.class)
public class IdDaoTest {
  static PersistenceManagerFactory pmf;
  List<Note> toBeDeleted = Lists.newArrayList();

  // Class under test.
  NoteDao dao;

  @BeforeClass
  public static void beforeAll() throws Exception {
    pmf = JDOHelper.getPersistenceManagerFactory("mongodbtest");
  }

  @Before
  public void beforeEach() throws Exception {
    dao = new NoteDao(pmf);
  }

  @After
  public synchronized void afterEach() throws Exception {
    Iterator<Note> iterator = toBeDeleted.iterator();
    while (iterator.hasNext()) {
      Note note = iterator.next();
      dao.delete(note);
      iterator.remove();
    }
  }

  @Test
  public void shouldPerformNoteCrudOps() throws Exception {
    Note note = newNote("Day 20", 20);
    dao.save(note);
    assertNotNull("note.metadata should not be null.", note.metadata);

    long noteId = note.id;
    long metadataId = note.metadata.id;

    // Make sure the ids were set.
    assertTrue("note.id should be set.", noteId > 0);
    assertTrue("note.metadata.id should be set.", metadataId > 0);

    // There's a config setting that will result in properties being nulled
    // out. There's also a query field that needs to be set to tell the API to
    // include nested objects. These asserts verify both although the former
    // isn't a very useful check since the test code uses different configs
    // than production.
    Note expected = newNote("Day 20", 20);
    expected.id = noteId;
    expected.metadata.id = metadataId;
    assertObjectsEqual(expected, note);

    // Make sure we can fetch the objects from the db.
    assertObjectsEqual(expected, dao.lookup(Note.class, note.id));
    assertObjectsEqual(expected.metadata, dao. lookup(NoteMetadata.class, note.metadata.id));

    // Make sure update works.
    note.text = "Updated text. Jack is not a dull boy now.";
    dao.save(note);
    expected.text = note.text;
    assertEquals(expected, note);

    // Make sure delete works.
    dao.delete(note);
    assertNull("Should delete note.", dao.lookup(Note.class, noteId));
    assertNull("Should delete note.metadata.", dao.lookup(NoteMetadata.class, metadataId));
  }

  @Test
  public void shouldSearchForNotes() throws Exception {
    List<Note> notes = Lists.newArrayList(
        save(newNote("Day 20", 20)),
        save(newNote("Day 21", 21)),
        save(newNote("Day 30", 30)));

    // Simple search.
    assertContentsInOrder(
        "Should perform simple search",
        dao.search(Selector.builder().withType(Note.class).withFilter("id > 0").build()),
        notes.get(0),
        notes.get(1),
        notes.get(2));

    // JDO does not support contains.
    // assertContentsInOrder(
    //    dao.search(Selector.builder()
    //        .withType(Note.class)
    //        .withFilter("metadata.title.contains(text)")
    //        .withParams(ImmutableMap.<String, Object>of("String text", "Day 2"))
    //        .build()),
    //    notes.get(0),
    //    notes.get(1));

    assertContentsInOrder(
        "Should search by matches.",
        dao.search(Selector.builder()
            .withType(Note.class)
            .withFilter("metadata.title.matches(text)")
            .withParams(ImmutableMap.<String, Object>of("String text", "Day 2.*"))
            .build()),
        notes.get(0),
        notes.get(1));

    assertContentsInOrder(
        "Should search by title equals.",
        dao.search(Selector.builder()
            .withType(Note.class)
            .withFilter("metadata.title == day21")
            .withParams(ImmutableMap.<String, Object>of("String day21", "Day 21"))
            .build()),
        notes.get(1));
  }

  @Test
  public void shouldSearchForNoteMetadatas() throws Exception {
    List<Note> notes = Lists.newArrayList(
        save(newNote("Day 20", 20)),
        save(newNote("Day 21", 21)),
        save(newNote("Day 30", 30)));

    assertContentsInOrder(
        "Should search for metadatas.",
        dao.search(Selector.builder().withType(NoteMetadata.class).build()),
        notes.get(0).metadata,
        notes.get(1).metadata,
        notes.get(2).metadata);
  }

  @Test
  public void withIds() throws Exception {
    List<Note> notes = Lists.newArrayList(
        save(newNote("Day 1", 1)),
        save(newNote("Day 002", 2)),
        save(newNote("Day 003", 3)),
        save(newNote("Day 4", 4)),
        save(newNote("Day 5", 5)));

    // This really needs to be assert in any order.
    // These could be flaky.
    assertContentsInOrder(
        "Should search by ids.",
        dao.search(Selector.builder()
            .withType(Note.class)
            .withIds(notes.get(1).id, notes.get(3).id)
            .build()),
        notes.get(3),
        notes.get(1));

    assertContentsInOrder(
        "Should search metadatas by ids.",
        dao.search(Selector.builder()
            .withType(NoteMetadata.class)
            .withIds(notes.get(1).metadata.id)
            .build()),
        notes.get(1).metadata);

    assertContentsInOrder(
        "Should search by ids and filter.",
        dao.search(Selector.builder()
            .withType(Note.class)
            .withIds(notes.get(1).id, notes.get(2).id, notes.get(3).id)
            .withFilter("metadata.title.matches('Day 00.*')")
            .build()),
        notes.get(1),
        notes.get(2));
  }

  @Test
  public void sorting() {
    List<Note> notes = Lists.newArrayList(
        save(newNote("Day 30", 30)),
        save(newNote("Day 21", 21)),
        save(newNote("Day 22", 22)),
        save(newNote("ABC", 22)),
        save(newNote("Day 09", 9)));

    assertContentsInOrder(
        "Should sort by lastModified asc.",
        dao.search(Selector.builder()
            .withType(Note.class)
            .withOrdering("metadata.lastModified asc")
            .build()),
        notes.get(4),
        notes.get(1),
        notes.get(2),
        notes.get(3),
        notes.get(0));

    assertContentsInOrder(
        "Should sort by lastModified desc then metadata.title asc.",
        dao.search(Selector.builder()
            .withType(Note.class)
            .withOrdering("metadata.lastModified desc, metadata.title asc")
            .build()),
        notes.get(0),
        notes.get(3),
        notes.get(2),
        notes.get(1),
        notes.get(4));
  }

  @Test
  public void pagination() {
    List<Note> notes = Lists.newArrayList(
        save(newNote("Day 1", 1)),
        save(newNote("Day 2", 2)),
        save(newNote("Day 3", 3)),
        save(newNote("Day 4", 4)),
        save(newNote("Day 5", 5)));

    assertContentsInOrder(
        "Should search for the first page.",
        dao.search(Selector.builder()
            .withType(Note.class)
            .withOrdering("metadata.lastModified asc")
            .skip(0)
            .take(3)
            .build()),
        notes.get(0),
        notes.get(1),
        notes.get(2));

    assertContentsInOrder(
        "Should search for the second page.",
        dao.search(Selector.builder()
            .withType(Note.class)
            .withOrdering("metadata.lastModified asc")
            .skip(3)
            .take(3)
            .build()),
        notes.get(3),
        notes.get(4));
  }

  @Test
  public void lookupByNoteMetadataId() throws Exception {
    Note note1 = save(newNote("Day 1", 1));
    save(newNote("Day 2", 2));
    assertTrue("note was not saved correctly.", note1.metadata.id > 0);
    assertObjectsEqual(note1, dao.lookupByNoteMetadaId(note1.metadata.id));
  }

  @Test
  public void lookupByNoteMetadataIdShouldBeNull() throws Exception {
    save(newNote("Day 1", 1));
    save(newNote("Day 2", 2));
    assertNull(
        "lookup on invalid ID should return null",
        dao.lookupByNoteMetadaId(Integer.MAX_VALUE));
  }

  private void assertContentsInOrder(String message, List<?> actuals, Object... expecteds) {
    assertEquals(message, expecteds.length, actuals.size());
    assertEquals(
        message,
        Lists.newArrayList(expecteds).toString(),
        Lists.newArrayList(actuals).toString());
  }

  private void assertObjectsEqual(Object expected, Object actual) {
    // The JDO enhancer does weird things to these objects that make it
    // difficult to compare this object with reflection. :\
    // assertTrue(
    //     String.format("Expected:%n%s%n%nbut was:%n%n%s%n", expected, actual),
    //     EqualsBuilder.reflectionEquals(expected, actual));
    assertEquals(expected.toString(), actual.toString());
  }

  private Note newNote(String title, int day) {
    Note note = new Note();
    note.text = Strings.repeat("All work and no play makes Jack a dull boy. ", 20).trim();
    note.metadata = new NoteMetadata();
    note.metadata.title = title;
    note.metadata.lastModified = new DateTime(1980, 5, day, 0, 0, DateTimeZone.UTC).toDate();
    return note;
  }

  private Note save(Note note) {
    dao.save(note);
    toBeDeleted.add(note);
    return note;
  }
}
