package org.acme.gradle;

import org.gradle.api.*;
import org.gradle.api.tasks.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class MyJavaTask extends DefaultTask {
  @TaskAction
  public void javaTask() {
    try {
      Files.write(
          Paths.get(System.getProperty("user.home"), "Desktop/gradle_test.txt"),
          Arrays.asList(String.format("Hello from %s.", getClass().getName())),
          StandardCharsets.UTF_8);
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
}
