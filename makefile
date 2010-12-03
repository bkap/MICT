STORE = rdebasekeystore
PASSWD = $(shell cat .passwd.rdebasekeystore)
SSL_DEBUG = -Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Djavax.net.debug=ssl
KEY_PASSWD = -Djavax.net.ssl.keyStorePassword=$(PASSWD)
KEY_STORE = -Djavax.net.ssl.keyStore=$(STORE)
TRUST_PASSWD = -Djavax.net.ssl.keyStorePassword=$(PASSWD)
TRUST_STORE = -Djavax.net.ssl.trustStore=$(STORE)
CLASSPATH = -classpath .:jython.jar:postgresql.jar
BUILD_OPTIONS = -Xlint:unchecked -Xlint:deprecation
DEBUG = 

clean:
	rm -rf mict/client/*.class
	rm -rf mict/server/*.class
	rm -rf mict/networking/*.class
	rm -rf mict/tools/*.class
	rm -rf mict/bridge/*.class
	rm -rf mict/test/*.class
	rm -rf mict/util/*.class
	rm -rf RDETest.class

build:			buildclient buildserver
	
buildclient:	buildcommon
	javac $(CLASSPATH) $(BUILD_OPTIONS) mict/client/*.java

buildserver:	buildcommon
	javac $(CLASSPATH) $(BUILD_OPTIONS) mict/server/*.java

buildcommon:
	javac $(CLASSPATH) $(BUILD_OPTIONS) mict/networking/*.java
	javac $(CLASSPATH) $(BUILD_OPTIONS) mict/tools/*.java
	javac $(CLASSPATH) $(BUILD_OPTIONS) mict/bridge/*.java
	javac $(CLASSPATH) $(BUILD_OPTIONS) mict/test/*.java
	javac $(CLASSPATH) $(BUILD_OPTIONS) mict/util/*.java

server:		buildserver runserver

runserver:
	java $(CLASSPATH) $(KEY_STORE) $(KEY_PASSWD) $(DEBUG) mict.server.Server --config=conf/server.conf

client:		buildclient runclient

runclient:
	java $(CLASSPATH) $(TRUST_STORE) $(TRUST_PASSWD) $(DEBUG) mict.client.Client --config=conf/client.conf

buildrdetest:	build
	javac $(CLASSPATH) $(BUILD_OPTIONS) RDETest.java

runrdetest:
	java $(CLASSPATH) $(KEY_STORE) $(KEY_PASSWD) $(DEBUG) RDETest

rdetest:	buildrdetest runrdetest
