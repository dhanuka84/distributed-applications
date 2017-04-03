#!/bin/sh

java -cp "distributed-services-0.1-SNAPSHOT.jar:lib/*" -Dhazelcast.config=/home/uranadh/hazelcast.xml   org.reactor.monitoring.application.internal.Member
