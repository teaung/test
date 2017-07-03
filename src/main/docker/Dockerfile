FROM 10.5.133.220:5000/openjdk:8u121-jre-alpine
VOLUME /tmp
ADD serv05-traincontrol-v0.2.0.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]