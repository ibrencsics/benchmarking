#!/bin/bash
java -cp target/netty-simple-http-server-1.0-SNAPSHOT-jar-with-dependencies.jar -Dlog4j.configurationFile=src/main/resources/log4j2.properties org.ib.eval.netty.http.server.Server $1
