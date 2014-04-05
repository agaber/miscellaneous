package com.acme.notepad.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.xml.bind.annotation.XmlRootElement;

import com.acme.notepad.common.StringUtil;

/** A note is arbitrary user entered text and some additional metadata. */
@PersistenceCapable(detachable = "true")
@XmlRootElement
public class Note extends Id {
  public String text;
  public NoteMetadata metadata;

  @Override
  public String toString() {
    return StringUtil.toString(this);
  }
}
