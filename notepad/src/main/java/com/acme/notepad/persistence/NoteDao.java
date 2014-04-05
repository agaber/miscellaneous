package com.acme.notepad.persistence;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jdo.FetchGroup;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.acme.notepad.common.StringUtil;
import com.acme.notepad.model.Id;
import com.acme.notepad.model.Note;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Performs basic database operations on {@link Id}, {@link Note} and
 * {@link NoteMetadata} objects.
 *
 * <p>Note to self: This app assumes that all objects are "detatched" from the
 * database which requires a note in the annotations and either an xml config
 * setting or manually calling {@code pm.setDetachAllOnCommit(true);}. This
 * code assumes the former.
 *
 * <p>Note to self: In a real app I might create a generic dao since a lot of
 * these operations are consistent among most objects but I'd create a more
 * specific dao for my app's use cases. For example delete,
 * lookupByNoteMetadataId and I'd probably add methods for strongly typed
 * search (right now everything is just string queries).
 */
@Singleton
public class NoteDao {
  private final PersistenceManagerFactory pmf;

  @Inject
  public NoteDao(PersistenceManagerFactory pmf) {
    this.pmf = pmf;
  }

  /** Deletes a Note and it's metadata reference in a single transaction. */
  public void delete(Note note) {
    checkNotNull(note, "can't delete a null object");
    PersistenceManager pm = pmf.getPersistenceManager();
    Transaction transaction = pm.currentTransaction();
    try {
      transaction.begin();
      if (note.metadata != null && note.metadata.id > 0) {
        pm.deletePersistent(pm.getObjectById(note.metadata.getClass(), note.metadata.id));
      }
      pm.deletePersistent(pm.getObjectById(note.getClass(), note.id));
      transaction.commit();
    } finally {
      close(pm, transaction);
    }
  }

  /**
   * Looks up a {@link Id} object by ID and returns the full object.
   * Returns null if the object for the given ID couldn't be found.
   */
  public <T extends Id> T lookup(Class<T> type, long id) {
    checkArgument(id > 0, "can't look up an object by zero ID");
    PersistenceManager pm = pmf.getPersistenceManager();
    pm.getFetchPlan().setGroup(FetchGroup.ALL);
    try {
      return pm.getObjectById(type, id);
    } catch (JDOObjectNotFoundException e) {
      return null;
    } finally {
      pm.close();
    }
  }

  public Note lookupByNoteMetadaId(long metadataId) {
    checkArgument(metadataId > 0, "can't look up a Note with a zero metadata ID");
    List<Note> notes = search(Selector.builder()
        .withType(Note.class)
        .withFilter("metadata.id == " + metadataId)
        .build());
    return Iterables.getFirst(notes, null);
  }

  /** Saves an {@link Note} object. */
  public <T extends Id> void save(T o) {
    checkNotNull(o, "can't save a null object");
    PersistenceManager pm = pmf.getPersistenceManager();
    Transaction transaction = pm.currentTransaction();
    try {
      transaction.begin();
      pm.makePersistent(o);
      transaction.commit();
    } finally {
      close(pm, transaction);
    }
  }

  /** Searches for any arbitrary object based on the given selector. */
  public <T> List<T> search(Selector selector) {
    checkNotNull(selector, "can't search by null selector");
    PersistenceManager pm = pmf.getPersistenceManager();
    pm.getFetchPlan().setGroup(FetchGroup.ALL);
    Query query = pm.newQuery();
    try {
      String idsQuery = parseIds(selector.ids);
      if (!StringUtil.isEmpty(idsQuery)) {
        query.setFilter(idsQuery);
      }
      if (!StringUtil.isEmpty(selector.filter)) {
        String initial = !StringUtil.isEmpty(idsQuery) ? idsQuery + " && " : "";
        query.setFilter(initial + selector.filter);
      }
      if (!StringUtil.isEmpty(selector.ordering)) {
        query.setOrdering(selector.ordering);
      }
      if (!selector.params.isEmpty()) {
        query.declareParameters(Joiner.on(", ").join(selector.params.keySet()).trim());
      }
      if (selector.type != null) {
        query.setClass(selector.type);
      }
      query.setRange(selector.skip, selector.skip + selector.take);
      ImmutableMap<String, Object> params = parseParams(selector.params);
      @SuppressWarnings("unchecked")
      List<T> results = params.isEmpty()
          ? (List<T>) query.execute()
          : (List<T>) query.executeWithMap(params);
      return Lists.newArrayList(results);
    } finally {
       query.closeAll();
    }
  }

  private void close(PersistenceManager pm, Transaction transaction) {
    if (transaction.isActive()) {
      transaction.rollback();
    }
    pm.close();
  }

  private String parseIds(ImmutableSet<Long> ids) {
    List<String> queries = Lists.transform(Lists.newArrayList(ids), new Function<Long, String> () {
      @Override
      public String apply(Long id) {
        return String.format("id == %d", id);
      }
    });
    String result = Joiner.on(" || ").join(queries);
    return !result.isEmpty() ? String.format("(%s)", result) : "";
  }

  private ImmutableMap<String, Object> parseParams(ImmutableMap<String, Object> params) {
    ImmutableMap.Builder<String, Object> result = ImmutableMap.builder();
    for (String param : params.keySet()) {
      String paramName = param.split(" ")[1];
      result.put(paramName, params.get(param));
    }
    return result.build();
  }
}
