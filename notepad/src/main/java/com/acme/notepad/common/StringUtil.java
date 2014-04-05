package com.acme.notepad.common;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * A couple of handy methods for strings.
 * Class name Strings is already taken by guava.
 */
public final class StringUtil {
  /** Returns true if the string is null or if the trimmed string is empty. */
  public static boolean isEmpty(String s) {
    return s == null ? true : s.trim().isEmpty();
  }

  /** Returns the object as JSON String. */
  public static String toString(Object o) {
    if (o == null) {
      return null;
    }
    try {
      return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
    } catch (Exception e) {
      throw new AssertionError("JSON deserialization error.", e);
    }
  }
}
