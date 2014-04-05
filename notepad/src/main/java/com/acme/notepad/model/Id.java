package com.acme.notepad.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/** A persistent object that has a numeric ID. */
@PersistenceCapable
@XmlRootElement
public abstract class Id {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
  public long id;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    return Objects.equal(id, ((Id) obj).id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
