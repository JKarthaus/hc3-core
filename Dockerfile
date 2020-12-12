FROM arm32v7/adoptopenjdk:11-jre-openj9
COPY build/libs/hc3-core-*-all.jar hc3-core.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "hc3-core.jar" ]