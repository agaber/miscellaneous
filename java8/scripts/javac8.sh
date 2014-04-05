#!/bin/bash

# find src/main/java/ -name *.java | xargs ./javac8 -d target/classes

if [ ! -d target/classes ]; then
  mkdir -p target/classes
fi

../jdk1.8.0/bin/javac $@
