#!/bin/sh
java -jar jython.jar maketest.py
javac -cp .:junit.jar:jython.jar:hamcrest-core.jar ToolTest.java
java -cp .:junit.jar:jython.jar:hamcrest-core.jar org.junit.runner.JUnitCore ToolTest
