#!/bin/sh

# THIS SCRIPT REALLY JUST SERVERS AS A PLACEHOLDER, SO THAT I KNOW HOW JAVA DEALS WITH SSL CRAP
# EVENTUALLY, IT WILL ACTUALLY RESEMBLE AN INSTALL SCRIPT FOR UNIX ENVIRONMENTS

# IGNORE COMMENTS BELOW

# This script sets up some environment information under UNIX systems, provided all dependencies are met.

# go to correct directory. (which is ?)

# do this beforehand for each installation:
#keytool -genkey -keystore rdemictks -keyalg RSA

# move the key into the correct place

java -Djavax.net.ssl.keyStore=rdemictks -Djavax.net.ssl.keyStorePassword=password Server
java -Djavax.net.ssl.keyStore=rdemictks -Djavax.net.ssl.keyStorePassword=password client

# ugh. figure this all out later.
