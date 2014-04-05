package com.acme.notepad.model;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.xml.bind.annotation.XmlRootElement;

import com.acme.notepad.common.StringUtil;

/** Metadata about a note. */
@PersistenceCapable(detachable = "true")
@XmlRootElement
public class NoteMetadata extends Id {
  public String title;
  public Date lastModified;

  @Override
  public String toString() {
    return StringUtil.toString(this);
  }
}
