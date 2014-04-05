package com.acme.notepad.persistence;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.acme.notepad.common.StringUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/** Encapsulates the parameters needed for searching. */
public class Selector {
  /** Create a new builder capable of creating new Selector instances. */
  public static Builder builder() {
    return new Builder();
  }

  public final ImmutableSet<Long> ids;
  public final String filter;
  public final String ordering;
  public final ImmutableMap<String, Object> params;
  public final int skip;
  public final int take;
  public final Class<?> type;

  private Selector(
      ImmutableSet<Long> ids,
      String filter,
      String ordering,
      ImmutableMap<String, Object> params,
      int skip,
      int take,
      Class<?> type) {
    this.ids = ids;
    this.filter = filter;
    this.ordering = ordering;
    this.params = params;
    this.skip = skip;
    this.take = take;
    this.type = type;
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return StringUtil.toString(this);
  }

  public static class Builder {
    private ImmutableSet<Long> ids;
    private String filter;
    private String ordering;
    private ImmutableMap.Builder<String, Object> params;
    private int skip;
    private int take;
    private Class<?> type;

    public Builder() {
      ids = ImmutableSet.of();
      params = ImmutableMap.builder();
      take = 100;
    }

    public Builder withIds(Long... ids) {
      this.ids = ImmutableSet.copyOf(Sets.newHashSet(ids));
      return this;
    }

    public Builder withFilter(String filter) {
      this.filter = checkNotNull(filter, "filter can't be null");
      return this;
    }

    public Builder withOrdering(String ordering) {
      this.ordering = checkNotNull(ordering, "ordering can't be null");
      return this;
    }

    public Builder withParams(Map<String, Object> params) {
      checkNotNull(params, "params can't be null");
      this.params = ImmutableMap.builder();
      for (String key : params.keySet()) {
        checkArgument(
            !StringUtil.isEmpty(key) && key.matches("\\S* \\S*"),
            "param name '" + key + "' must be in the format 'type name'. For example 'Long myvar'");
        Object value = checkNotNull(
            params.get(key),
            String.format("value for key '%s' can't be null", key));
        this.params.put(key, value);
      }
      return this;
    }

    public Builder skip(int skip) {
      checkArgument(skip >= 0, "skip must be >= 0");
      this.skip = skip;
      return this;
    }

    public Builder take(int take) {
      checkArgument(take > 0, "take must be > 0");
      this.take = take;
      return this;
    }

    public Builder withType(Class<?> type) {
      this.type = checkNotNull(type, "type can't be null");
      return this;
    }

    public Selector build() {
      return new Selector(ids, filter, ordering, params.build(), skip, take, type);
    }
  }
}
