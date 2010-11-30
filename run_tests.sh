#!/bin/sh
#java -jar jython.jar maketest.py
javac -cp .:junit.jar:jython.jar:hamcrest-core.jar SerializeTest.java
java -cp .:junit.jar:jython.jar:hamcrest-core.jar org.junit.runner.JUnitCore SerializeTest
