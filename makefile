STORE = mictrdebasekeystore
PASSWD = $(shell cat .passwd.mictrdebasekeystore)
SSL_DEBUG = -Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Djavax.net.debug=ssl
KEY_PASSWD = -Djavax.net.ssl.keyStorePassword=$(PASSWD)
KEY_STORE = -Djavax.net.ssl.keyStore=$(STORE)
TRUST_PASSWD = -Djavax.net.ssl.keyStorePassword=$(PASSWD)
TRUST_STORE = -Djavax.net.ssl.trustStore=$(STORE)
CLASSPATH = -classpath /opt/mict/mict/:/opt/mict/mict/*:/usr/share/java/*
BUILD_OPTIONS = -Xlint:unchecked
DEBUG = 

build:
	javac $(CLASSPATH) $(BUILD_OPTIONS) mict/client/Client.java
	javac $(CLASSPATH) $(BUILD_OPTIONS) mict/server/Server.java

runserver:	build
	java $(CLASSPATH) $(KEY_STORE) $(KEY_PASSWD) $(DEBUG) mict.server.Server

runclient:	build
	java $(CLASSPATH) $(TRUST_STORE) $(TRUST_PASSWD) $(DEBUG) mict.client.Client
