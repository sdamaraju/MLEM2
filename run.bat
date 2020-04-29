Pushd "%~dp0"
cd src
javac -d ../classes *.java
cls
cd ../classes
java MLEM2Implementation
cd ..
