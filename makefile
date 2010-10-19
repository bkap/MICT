build:
	javac -classpath /opt/mict/mict/:/opt/mict/mict/* -Xlint:unchecked mict/client/Client.java
	javac -classpath /opt/mict/mict/:/opt/mict/mict/* -Xlint:unchecked mict/server/Server.java

run:	build
	java -classpath /opt/mict/mict/:/opt/mict/mict/* mict.client.Client

runserver:	build
	java -classpath /opt/mict/mict/:/opt/mict/mict/*:/usr/share/java/* mict.server.Server
