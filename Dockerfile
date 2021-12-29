FROM adoptopenjdk:11-jre-hotspot
MAINTAINER experto.com
VOLUME /tmp
EXPOSE 8080
ADD build/libs/taskmanager-0.0.1-SNAPSHOT.jar taskmanager.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/taskmanager.jar"]