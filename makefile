STORE = rdebasekeystore
PASSWD = $(shell cat .passwd.rdebasekeystore)
SSL_DEBUG = -Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Djavax.net.debug=ssl
KEY_PASSWD = -Djavax.net.ssl.keyStorePassword=$(PASSWD)
KEY_STORE = -Djavax.net.ssl.keyStore=$(STORE)
TRUST_PASSWD = -Djavax.net.ssl.keyStorePassword=$(PASSWD)
TRUST_STORE = -Djavax.net.ssl.trustStore=$(STORE)
CLASSPATH = -classpath .:jython.jar:/usr/share/java/*
BUILD_OPTIONS = -Xlint:unchecked
DEBUG = 

build:			buildclient buildserver
	
buildclient:	buildcommon
	javac $(CLASSPATH) $(BUILD_OPTIONS) mict/client/*.java

buildserver:	buildcommon
	javac $(CLASSPATH) $(BUILD_OPTIONS) mict/server/*.java

buildcommon:
	javac $(CLASSPATH) $(BUILD_OPTIONS) mict/networking/*.java
	javac $(CLASSPATH) $(BUILD_OPTIONS) mict/tools/*.java
	javac $(CLASSPATH) $(BUILD_OPTIONS) mict/bridge/*.java

runserver:		build
	java $(CLASSPATH) $(KEY_STORE) $(KEY_PASSWD) $(DEBUG) mict.server.Server

runclient:		buildclient
	java $(CLASSPATH) $(TRUST_STORE) $(TRUST_PASSWD) $(DEBUG) mict.client.Client
