package com.acme.myapp;

import static org.junit.Assert.*;
import static java.nio.file.Files.write;
import static java.util.Arrays.asList;

import com.google.jimfs.Configuration;
import com.google.jimfs.Jimfs;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Map;

public class EmailResourceTest {
  EmailResource resource;

  @Before
  public void beforeEach() throws Exception {
    Path path = Jimfs.newFileSystem(Configuration.unix()).getPath("/words.txt");
    write(path, asList("apple", "banana", "orange", "lemon"));
    resource = new EmailResource(path);
  }

  @Test
  public void list() throws Exception {
    assertArrayEquals(
        new String[] { "apple", "banana", "lemon", "orange" },
        resource.list().toArray());
  }

  @Test
  public void spellCheck() throws Exception {
    Map<String, Boolean> result = resource.spellCheck(asList(
        "apple",
        "lemoon",
        "Orange",
        "chocolate"));
    assertEquals(4, result.size());
    assertTrue(result.get("apple"));
    assertFalse(result.get("lemoon"));
    assertTrue(result.get("Orange"));
    assertFalse(result.get("chocolate"));
  }
}
