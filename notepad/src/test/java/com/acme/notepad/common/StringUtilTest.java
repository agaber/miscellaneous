package com.acme.notepad.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.acme.notepad.common.StringUtil;

public class StringUtilTest {

  @Test
  public void isEmpty() throws Exception {
    assertTrue(StringUtil.isEmpty(""));
    assertTrue(StringUtil.isEmpty("   "));
    assertTrue(StringUtil.isEmpty(null));
    assertFalse(StringUtil.isEmpty("test"));
  }

  @Test
  public void testToString() throws Exception{
    MyObject o = new MyObject();
    o.id = 123L;
    o.title = "TODO list";
    o.setText("1. Write unit tests.");
    assertEquals(String.format(
        "{%n" +
        "  \"id\" : 123,%n" +
        "  \"title\" : \"TODO list\",%n" +
        "  \"text\" : \"1. Write unit tests.\"%n" +
        "}"),
    StringUtil.toString(o));
  }

  private static class MyObject {
    @SuppressWarnings("unused")
    public long id;
    @SuppressWarnings("unused")
    public String title;
    private String text;

    @SuppressWarnings("unused")
    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }
  }
}
