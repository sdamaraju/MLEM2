#!/bin/bash
cd "$(dirname "$BASH_SOURCE")"/src
javac -d ../classes *.java
clear
cd ../classes
java MLEM2Implementation
cd ..
