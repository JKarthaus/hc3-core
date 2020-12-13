FROM adoptopenjdk:15-jre-hotspot
COPY build/libs/hc3-core-*-all.jar hc3-core.jar
EXPOSE 8080
CMD  java -jar hc3-core.jar
