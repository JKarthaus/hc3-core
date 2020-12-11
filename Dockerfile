FROM adoptopenjdk/openjdk11:armv7l-ubuntu-jre-11.0.8_10
COPY build/libs/hc3-core-*-all.jar hc3-core.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-XX:+IdleTuningGcOnIdle", "-Xtune:virtualized", "-jar", "hc3-core.jar"