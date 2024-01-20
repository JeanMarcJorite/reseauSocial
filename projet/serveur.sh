#!/bin/bash
cd src
javac -cp "../lib" *.java
cd ..
java -cp "bin" Server