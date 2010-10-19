build:
	javac -classpath /opt/mict/mict/:/opt/mict/mict/* -Xlint:unchecked mict/client/Client.java

run:	build
	java -classpath /opt/mict/mict/:/opt/mict/mict/* mict.client.Client
