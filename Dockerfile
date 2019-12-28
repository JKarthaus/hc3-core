FROM openjdk:8u171-alpine3.7

COPY build/libs/hc3-core-*-all.jar hc3-core.jar

EXPOSE 8080

CMD java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar hc3-core.jar