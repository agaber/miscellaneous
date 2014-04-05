#!/bin/bash

# Compile.
find src/main/java/ -name *.java | xargs ./scripts/javac8.sh -d target/classes

# Run.
./scripts/java8.sh -cp target/classes com.acme.sandbox.jdk8.Main
