package org.acme.gradle;

import org.gradle.api.*;

public class MyJavaPlugin implements Plugin<Project> {
  @Override
  public void apply(Project target) {
    target.task("javaTask");
  }
}
